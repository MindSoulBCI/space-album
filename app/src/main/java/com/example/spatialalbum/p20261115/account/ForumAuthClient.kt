package com.example.spatialalbum.p20261115.account

import android.content.Context
import com.example.spatialalbum.p20261115.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

data class AuthSession(val token: String, val userId: String, val email: String, val nickname: String)
class AuthFailure(message: String) : Exception(message)

/** 对接 mindsoul.cn/forum 的 auth 接口，响应格式为 { code, msg, data }。 */
class ForumAuthClient(private val baseUrl: String, private val appId: String) {
    private val base = baseUrl.trimEnd('/')

    init {
        val url = URL(base)
        require(url.protocol == "https" && url.userInfo == null) { "Account API must use HTTPS" }
        require(appId.isNotBlank()) { "appId required" }
    }

    private suspend fun post(path: String, body: JSONObject): JSONObject = withContext(Dispatchers.IO) {
        val connection = URL(base + path).openConnection() as HttpsURLConnection
        try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 12000
            connection.readTimeout = 20000
            connection.instanceFollowRedirects = false
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            connection.setRequestProperty("Accept", "application/json")
            connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val raw = stream?.use { it.readBytes() } ?: ByteArray(0)
            if (raw.size > 65536) throw AuthFailure("账号服务响应异常。")
            val json = if (raw.isEmpty()) JSONObject() else JSONObject(String(raw, Charsets.UTF_8))
            val code = json.optInt("code", if (status in 200..299) 200 else status)
            if (code != 200) {
                val msg = json.optString("msg").ifBlank { null }
                throw AuthFailure(msg ?: when (status) {
                    401, 403 -> "登录凭据无效或会话已过期，请重新登录。"
                    409 -> "该邮箱无法注册，请尝试登录。"
                    429 -> "操作过于频繁，请稍后再试。"
                    400, 422 -> "信息或验证码无效，请检查后重试。"
                    else -> "账号服务暂时不可用，请稍后重试。"
                })
            }
            json.optJSONObject("data") ?: JSONObject()
        } catch (e: AuthFailure) {
            throw e
        } catch (_: Exception) {
            throw AuthFailure("无法连接账号服务，请检查网络后重试。")
        } finally {
            connection.disconnect()
        }
    }

    private fun payload(vararg fields: Pair<String, String>) = JSONObject().apply {
        put("appId", appId)
        fields.forEach { (k, v) -> put(k, v) }
    }

    private fun session(data: JSONObject): AuthSession {
        val token = data.optString("token").ifBlank { data.optString("accessToken") }
        if (token.isBlank() || token.any { it == '\r' || it == '\n' }) throw AuthFailure("账号服务返回了无效会话。")
        return AuthSession(
            token = token,
            userId = data.opt("userId")?.toString().orEmpty(),
            email = data.optString("email"),
            nickname = data.optString("nickname").ifBlank { data.optString("email").substringBefore('@').ifBlank { "用户" } },
        )
    }

    suspend fun sendCode(email: String) {
        post("/auth/sendVerifyCode", payload("email" to email))
    }

    suspend fun register(email: String, password: String, code: String) = session(
        post(
            "/auth/signup",
            payload(
                "email" to email,
                "password" to password,
                "rePassword" to password,
                "verifyCode" to code,
            ),
        ),
    )

    suspend fun login(email: String, password: String) = session(
        post("/auth/login", payload("email" to email, "password" to password)),
    )

    suspend fun loginByCode(email: String, code: String) = session(
        post("/auth/loginByCode", payload("email" to email, "verifyCode" to code)),
    )

    companion object {
        fun from(context: Context): ForumAuthClient? {
            val base = context.getString(R.string.mindsoul_account_api).trim()
            val appId = context.getString(R.string.mindsoul_app_id).trim()
            if (base.isEmpty() || appId.isEmpty()) return null
            return try {
                ForumAuthClient(base, appId)
            } catch (_: Exception) {
                null
            }
        }

        fun email(value: String) = value.trim().lowercase()
        fun validEmail(v: String) = v.length <= 254 && Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(v.trim())
        fun validPassword(v: String) = v.length in 6..20 && v.all { it.isLetterOrDigit() }
        fun validCode(v: String) = Regex("^[0-9]{6}$").matches(v)
    }
}

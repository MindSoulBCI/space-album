package com.example.spatialalbum.p20261115.ui.account
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.design.windows.AlertDialog
import com.example.spatialalbum.p20261115.account.AuthFailure
import com.example.spatialalbum.p20261115.account.ForumAuthClient
import kotlinx.coroutines.launch

@Composable fun AccountDialog(onDismiss:()->Unit,onSuccess:(()->Unit)?=null) {
 val context=LocalContext.current
 val client=remember{ForumAuthClient.from(context)}
 val scope=rememberCoroutineScope()
 var register by remember {mutableStateOf(false)}
 var passwordLogin by remember {mutableStateOf(false)}
 var email by remember {mutableStateOf("")};var code by remember {mutableStateOf("")}
 var password by remember {mutableStateOf("")};var confirm by remember {mutableStateOf("")}
 var accepted by remember {mutableStateOf(false)}
 var busy by remember {mutableStateOf(false)}
 var codeHint by remember {mutableStateOf("")}
 var policy by remember {mutableStateOf<String?>(null)}
 var message by remember {mutableStateOf("")}
 fun api():ForumAuthClient=client?:throw AuthFailure("账号服务尚未配置。")
 if(policy!=null) {PolicyDialog(policy=="privacy"){policy=null};return}
 AlertDialog(onDismissRequest=onDismiss,title={Text(if(register) "注册空间相册" else "邮箱登录")},content={
  Column(Modifier.width(480.dp).heightIn(max=380.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)) {
   Text("让你的空间记忆都在这里...")
   if(!register) Button(enabled=!busy,onClick={passwordLogin=!passwordLogin;message="";password=""}){Text(if(passwordLogin)"使用邮箱验证码"else"使用密码登录")}
   TextField(value=email,onValueChange={email=it;message="";codeHint=""},placeholder={Text("邮箱地址")},singleLine=true,modifier=Modifier.fillMaxWidth(),enabled=!busy)
   if(register||!passwordLogin){
    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
     TextField(value=code,onValueChange={code=it.filter(Char::isDigit).take(6)},placeholder={Text("6 位验证码")},singleLine=true,modifier=Modifier.weight(1f),enabled=!busy)
     Button(enabled=!busy,onClick={
      busy=true
      scope.launch{
       try{
        val mail=ForumAuthClient.email(email)
        if(!ForumAuthClient.validEmail(mail)){message="请输入有效邮箱";return@launch}
        api().sendCode(mail)
        codeHint="验证码已发送到邮箱，请查收。"
        message=""
       }catch(e:AuthFailure){message=e.message?:"发送失败"}
       catch(_:Exception){message="无法连接账号服务，请检查网络"}
       finally{busy=false}
      }
     }) {Text("获取验证码")}
    }
    if(codeHint.isNotEmpty()) Text(codeHint,style=PicoTheme.typography.bodySmall)
   }
   if(register||passwordLogin) {
    TextField(value=password,onValueChange={password=it},placeholder={Text(if(register)"设置密码（6–20 位字母或数字）"else"密码")},visualTransformation=PasswordVisualTransformation(),singleLine=true,modifier=Modifier.fillMaxWidth(),enabled=!busy)
   }
   if(register) {
    TextField(value=confirm,onValueChange={confirm=it},placeholder={Text("再次输入密码确认")},visualTransformation=PasswordVisualTransformation(),singleLine=true,modifier=Modifier.fillMaxWidth(),enabled=!busy)
   }
   Text("验证码由服务端发送到邮箱",style=PicoTheme.typography.bodySmall)
   Row(verticalAlignment=Alignment.CenterVertically) {
    Checkbox(checked=accepted,onCheckedChange={accepted=it},enabled=!busy)
    Text("我已阅读并同意",style=PicoTheme.typography.bodySmall)
   }
   Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
    Button(onClick={policy="terms"}) {Text("用户协议")}
    Button(onClick={policy="privacy"}) {Text("隐私政策")}
   }
   if(message.isNotEmpty()) Text(message)
   Button(enabled=!busy,onClick={
    if(!accepted){message="请先阅读并同意用户协议与隐私政策";return@Button}
    busy=true
    scope.launch{
     try{
      val mail=ForumAuthClient.email(email)
      if(!ForumAuthClient.validEmail(mail)){message="请输入有效邮箱";return@launch}
      if(register){
       if(!ForumAuthClient.validPassword(password)){message="密码需为 6–20 位字母或数字";return@launch}
       if(password!=confirm){message="两次输入的密码不一致";return@launch}
       if(!ForumAuthClient.validCode(code)){message="请输入 6 位邮箱验证码";return@launch}
       api().register(mail,password,code)
      }else if(passwordLogin){
       if(password.isEmpty()){message="请输入密码";return@launch}
       api().login(mail,password)
      }else{
       if(!ForumAuthClient.validCode(code)){message="请输入 6 位邮箱验证码";return@launch}
       api().loginByCode(mail,code)
      }
      onSuccess?.invoke();onDismiss()
     }catch(e:AuthFailure){message=e.message?:"操作失败"}
     catch(_:Exception){message="无法连接账号服务，请检查网络"}
     finally{busy=false}
    }
   },modifier=Modifier.fillMaxWidth()) {Text(if(busy)"处理中…"else if(register) "注册" else "登录")}
  }
 },buttons={Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
   Button(enabled=!busy,onClick={register=!register;password="";confirm="";code="";codeHint="";message=""}) {Text(if(register) "已有账号，去登录" else "没有账号，去注册")}
 Button(onClick=onDismiss){Text("关闭")}
 }})
}

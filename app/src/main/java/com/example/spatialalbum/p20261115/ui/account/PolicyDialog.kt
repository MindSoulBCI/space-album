package com.example.spatialalbum.p20261115.ui.account
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.design.windows.AlertDialog
@Composable fun PolicyDialog(privacy:Boolean,onDismiss:()->Unit) {
 AlertDialog(onDismissRequest=onDismiss,title={Text(if(privacy) "隐私政策" else "用户协议")},content={
  Column(Modifier.width(520.dp).heightIn(max=340.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(16.dp)) {
   Text("空间相册 · 2026 年 11 月",style=PicoTheme.typography.bodySmall)
   Text("服务提供方：成都脑云矩阵科技有限公司。品牌支持：MindSoul 脑机接口基座大模型。")
   if(privacy) {
    Text("1. 本机内容：你主动选择导入的照片或视频会复制到本应用的本机存储，用于缩略图、播放和沉浸展示。照片深度与左右眼图像在设备本地生成；当前应用不主动上传这些内容。")
    Text("2. 邮箱账号：注册与登录通过脑云矩阵账号服务（https://mindsoul.cn/forum）完成，邮箱验证码由服务端发送。")
    Text("3. 游客体验：无需提供邮箱和密码，可浏览示例内容并体验本机功能。示例照片与视频的来源见项目素材说明。游客模式不提供云同步。")
    Text("4. 容量和权限：应用读取自身媒体文件大小和设备存储容量以显示占用；通过系统文件选择器读取你指定的文件，不扫描全部相册。当前不采集真实脑电，不连接脑机接口设备。你可主动开启演示任务并勾选保存，将合成的三通道演示信号保存为本机 CSV；文件明确标注为模拟数据。")
    Text("5. 数据管理：可删除相册中的副本，原始文件不受影响。清除应用数据或卸载会移除本机副本和生成结果。操作系统备份与恢复遵循设备设置。")
    Text("6. 外部页面：主动点击 MindSoul 官网后，由浏览器打开外部网站，该网站的数据处理规则需另行查看。")
   } else {
    Text("1. 服务范围：空间相册提供本机照片管理、基于深度估计的空间照片、视频播放与沉浸画廊。你可以选择游客模式，或使用邮箱注册登录脑云矩阵账号。生成结果为推断的立体效果，可能存在遮挡和边缘误差。")
    Text("2. 注册与登录：默认采用邮箱验证码；注册还需设置密码并再次确认。也可以使用密码登录。")
    Text("3. 使用内容：请仅导入你有权使用的照片或视频，尊重他人的隐私、肖像和知识产权。示例素材用于当前产品体验，不代表获得其独立商业再分发授权。")
    Text("4. 保存与删除：本版本内容保存在设备中，无云端同步或账号恢复承诺。请自行保留重要原件；删除、清除数据或卸载前确认备份。")
    Text("5. 空间体验：体验时保持周围活动空间安全；如感到眩晕或不适，请暂停。品牌支持说明不表示本版本已连接脑机接口或调用云端模型。")
   }
  }
 },buttons={Button(onClick=onDismiss){Text("返回登录 / 注册")}})
}

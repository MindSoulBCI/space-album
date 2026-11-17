package com.example.spatialalbum.p20261115.domain.model
enum class ThoughtEmotion(val label:String,val scene:String,val sample:String) {
 HAPPY("快乐","温暖明亮的海边日落，金色光线，开阔构图","photo-01"),
 CALM("平静","平静湖面与远山倒影，柔和蓝绿色，简洁构图","photo-14"),
 EXCITED("兴奋","日出云海，鲜明暖色与充满活力的光线","photo-03"),
 CURIOUS("好奇","通往未知的森林小径，丰富细节与层层景深","photo-02"),
 SLEEPY("困倦","薄雾中的静谧森林，低对比柔光，梦境氛围","photo-17"),
 SAD("悲伤","阴天海岸，低饱和冷色，细腻安静的氛围","photo-08"),
 ANXIOUS("焦虑","宽阔天空与平缓水面，柔和色彩，舒缓留白","photo-11"),
 ANGRY("生气","瀑布与岩石，强烈层次逐渐过渡为平和光线","photo-07"),
 LONELY("孤独","宁静山谷里的一束温暖光线，留白与远景","photo-16")
}
fun thoughtRecipe(description:String,emotion:ThoughtEmotion,focus:Int,useEmotion:Boolean):String {
 val value=focus.coerceIn(0,100)
 val style=if(useEmotion) "情绪风格：${emotion.label}。${emotion.scene}。" else "专注度参考：$value%。"+when {
  value<34->"柔和色彩，宽阔视野，简洁主体与舒缓节奏。"
  value<67->"清晰主体，均衡构图，中等细节，柔和的前后景层次。"
  else->"鲜明主体，精细纹理，明确视觉焦点，丰富但有序的景深。"
 }
 return "${description.trim().ifEmpty {"创作一幅自然风景"}}。$style 前景、中景、远景清晰分层，适合生成空间照片。"
}

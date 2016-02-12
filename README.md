# MyChat<br />
基于Http的即时通讯软件<br />
最低兼容版本：android 4.0<br />
基本原理：<br />
服务器与客户端采用http协议传输。<br />
客户端定期查询服务器端，若有新的消息，则显示出来。<br />
客户端向服务器请求大部分采用GET方法，只有发送消息，设置头像时要上传数据才使用POST方法。<br />
服务器传回客户端JSON格式的数据，图像采用BASE64编码放到JSON中。<br />
已实现功能：<br />
好友管理：查找好友、申请好友、批准/拒绝、删除好友、查看好友详细信息<br />
消息：发送文字，表情（需要从输入法选择emoji表情）、发送图片（长按发送按钮）、删除当前会话<br />
个人设置：注册、登录、退出当前用户、设置头像、昵称、个性签名等<br />
尚未实现功能：朋友圈、黑名单管理、密码修改<br />

<p>
	服务器端源码：https://github.com/pwrliang/MyChatServer
</p>
<p>
	<br />
	
</p>
<p>
	截图：
</p>
<p>
	<img src="https://raw.githubusercontent.com/pwrliang/MyChat/master/DEMO/Screenshot_2016-02-12-17-58-35_com.gl.mychatclient.png" alt="" /><br />
	
</p>
<p>
	<img src="https://raw.githubusercontent.com/pwrliang/MyChat/master/DEMO/Screenshot_2016-02-12-17-58-14_com.gl.mychatclient.png" alt="" />
</p>
<p>
	<img src="https://raw.githubusercontent.com/pwrliang/MyChat/master/DEMO/Screenshot_2016-02-12-18-01-02_com.gl.mychatclient.png" alt="" /><br />
	
</p>
<p>
	<br />
	
</p>
<p>
	<img src="https://raw.githubusercontent.com/pwrliang/MyChat/master/DEMO/Screenshot_2016-02-12-18-04-15_com.gl.mychatclient.png" alt="" /><br />
	
</p>

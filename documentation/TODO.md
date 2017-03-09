### 新API的使用

#### UserInfoActivity

所需新布局：

	1. BadgeGrid - 徽章
	2. ActionList - 包含查看参加的社团，添加的好友等操作
	3. 参加社团列表 - 直接使用MainBrowser的Drawer里的布局
	4. 好友列表布局以及item布局



#### ClubSquareActivity

改用最新API，进入详细页面时使用新API获取社团信息。

所需修改布局：

​	ClubInfoDetailActivity



#### MainBrowser

*舍弃*Mainbrowser布局（停用或不推荐使用），使用新API获取主页JSON进行渲染

所需新布局：

​	ShiYiQuanIndexActivity
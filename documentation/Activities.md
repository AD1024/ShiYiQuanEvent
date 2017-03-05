### 现有Activity:

- MainActivity: 活动列表
- SplashScreen: 启动界面
- LoginActivity: 登录界面
- UserInfoActivity: 用户信息界面
- FavEventActivity: 活动收藏界面
- MainBrowser: 与十一圈对接的界面
- ClubSquareActivity: 社团广场
    - ClubInfoDetailActivity: 社团详细页面
- MomentActivity: 天台动态
- SettingActivity: 设置界面
- ScannerActivity: 二维码扫描
- about_me: 关于我
- OpenSourceInfo: 开源许可信息



### onActivityResult 请求码与结果码

* RequestCode:

>LoginRequestCode: 8080
>
>LogoutRequestCode: 8090
>
>QRCodeRequestCode: 6666
>
>PhotoPickerRequestCode: 7777

* ResultCode:

>LoginSuccessResult: 8081
>
>LogoutSucessResult: 8091
>
>QRCodeSucessResult: 6666
>
>QRCodeFailResult: 9999
>
>PhotoPickerSuccessResult: Activity.RESULT_OK


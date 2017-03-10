###  全局常量
全局常量用于各个文件中频繁使用的常量（如：十一圈主页链接），所有的常量存储于global包下。

### 常量说明
#### URLConstants
  * HOME_URL: 十一圈主页链接（用于请求api）;
  * HOME_URL_WITHOUT_DASH: 链接末尾不带斜线；
  * MOMENT_URL: 天台动态URL（使用beta.shiyiquan.net防止主页崩溃）
  * MOMENT_URL_ALTERNATIVE(未使用)：若beta崩溃切换主站api;
  * FINAL_VERSION_URL_PREFIX: 下载文件使用的container的URL前缀；
  * USER_AGENT: API请求用的APP_KEY，未加密储存；
  * QUARE_URL：社团广场API请求地址；
  * EVENT_URL：最新活动API请求地址；
  * DEFAULT_AVATAR_URL：用于没有设置头像的人；
  * CURRENT_VERSION：当前版本号，用于更新判断；
  * CSRF_PREFIX(deprecated)：用于抓取页面的csrf_token

#### PreferencesConstants
  **PreferenceName**
  * LOGIN_INFO：存储登录用户的信息；
  * HOST_ID_PREF：存储用户的host_id，在“转到十一圈”界面中用于身份判断；
  * SETTING_PREF：用于存储用户的设置信息；
  * UPDATE_CHECKER_PREF：用于存储更新信息；

  @施工中
  **Preferences**
  

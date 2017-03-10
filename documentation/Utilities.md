@hdy created on Feb 28
### Utils - 通用工具类
	* File getCacheFileDir(Context) 获取缓存文件
	* String ReadStringFromInputStream(InputStream) 处理输入流中的字符串
	* List<EventBean> parseEvent(JSONArray) 解析全部活动信息
	* String cleanAvatarUrl(String url, String middleFix) 将头像；链接地址中的middeFix删除，用于获取高清头像
	* List<MomentDataModel> parseMoment(JSONObject) 从JSONObject中获取天台信息
	* String Int2String(int) 将整数转换为字符串
	* String Double2String(double) 将小数转为字符串
	* int fromTextGetColor(String) 将bootstrap的颜色标志转换为整数(Color)
	* boolean isNetWorkAvailable(Context) 用于判断网络是否可用
	* String generateSign(Map<String,String>) 通过GET参数生成签名值，用于请求加密
	* long mogic(long,int) 加密算法(由于涉及到服务器安全性问题故不提供具体说明)
	* String makeRequest(String,String[],String[]) 通过传入子链接(收尾不带斜线)和参数key与values构造加密的GET请求

#### GetParam - 通用工具类 - GET参数容器
	* key 键
	* value 值
	* weight 权

#### container 辅助实现加密算法(由于涉及到服务器安全性问题故不提供具体说明)

---

### ToastUtil - 辅助实现Toast(不重复生成Toast)
	**本Util是单例工具**
	* Toast getInstance(Context) 获得实例
	* void initialize(Context) 若当前Activity需要使用ToastUtil，请先调用该方法注册将ToastUtil注册至该Activity的上下文。(E.g: ToastUtil.initialize(MainActivity.this);)
	* void makeText(String, boolean) 在当前界面显示一条Toast消息，重复调用该方法不会导致Toast重叠
	* void cancel() 取消该页面的Toast消息

---

### ViewTools - 简单的视图工具
	* View Inflate(Context,int,ViewGroup) 替代View.Inflate提高版本兼容性的手动视图解析器
	* MakeToast(Context,String,boolean) 创建一个Toast对象
	@deprecated
	* Toast ToastInfo(Context,String,boolean) 已过期的Toast工具

---

### ImageTools - 图像处理工具

	* int[] randomColor() 随机颜色工具，返回RGB数组
	* boolean isDeepColor(int[]) 通过RGB数组判断是否是深色
	* boolean isDeepColor(int,int,int) 通过传入RGB判断是否是深色
	* boolean isDeepColor(Bitmap) 传入一个图片矩阵判断是否是深色
	* Bitmap fastBlur(Bitmap,int) 传入一个图片矩阵和模糊半径，返回高斯模糊图片
	* Bitmap CompressBitmap(Bitmap,Bitmap.CompressFormat) 传入Bitmap，返回一个压缩至100k以下的Bitmap
	* Bitmap toBitmap(BitMatrix) 将ZXing的BitMatrix转换为原生的Bitmap
	* Bitmap String2QR(String,BarcodeFormat) 将文字转换为二维码

---

### DownloadUtil - 下载管理工具
	**本Util是单例工具**
	* DownloadManager getInstance() 获得实例
	* void initialize(Context) 与ToastUtil用法相同
	* void startDownload(String url, String title, String description) 通过Url直接建立下载任务
	* void startDownload(DownloadManager.Request) 通过包装好的Request类创建下载任务
	* boolean isInQueue(Long id) 判断下载任务是否在下载队列中
	* Uri getUriById(Long id) 通过任务id获得下载链接

#### RequestBuilder - 下载管理工具 - Request构造器
	* 构造函数：RequestBuilder(String) 通过Url初始化
	* setTitle(String) 设置下载任务标题
	* setDescription(String) 设置下载描述
	* setAllowedNetWorkType(int) 设置允许下载的网络环境
	* setEnableRoaming(boolean) 是否允许数据漫游
	* setVisibilityInUi(boolean) 是否显示在通知栏
	* setDownloadDirectory(File) 设置下载路径
	* setMimeType(String) 设置下载文件属性
	* DownloadManager.Request build() 构造请求

---

### MD5Util - MD5工具
	* HASH(String) 返回一个字符串的MD5

---

### MultiThreadUtil - 多线程工具
	* DefaultRetryPolicy createDefaultRetryPolicy() 构造一个默认断连重试策略
	* RetryPolicyBuilder
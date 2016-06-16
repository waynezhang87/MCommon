# MCommon
常用工具仓库

# Updates
* 06/16/2016 -- 1.0.0版本发布JCenter.

# Features
1. Http缓存策略
    * 目前支持的缓存策略定义在HttpPolicies类中, 包含:
        * POLICY_FORCE_NETWORK = 0;   //强制使用网络，跳过缓存
        * POLICY_CACHE_NETWORK_UPDATE_WITHOUT_CALLBACK = 1;    //先使用缓存，网络刷新缓存，但只有一次回调
        * POLICY_CACHE_NETWORK_UPDATE_CALLBACK = 2;   //先使用缓存，网络刷新缓存，有一次或两次回调
        * POLICY_NORMAL = 3; //网络超时，则使用缓存
        * POLICY_FORCE_CACHE = 4; //有缓存则不访问网络，无缓存则访问网络
    * 使用方法:
        * 建议在全局Application中启动时执行初始化方法Http.initCachePolicyHandler(Context context, HttpPolicyHandlerImp httpPolicyHandler, Init init)
        * 传入的对象HttpPolicyHandlerImp是配置对象, 用于配置Http请求的超时时间/缓存策略/使用的缓存, 使用HttpPolicyHandler中的builder类构建
        * 传入的对象Init用于Http请求获取的response的预解析, 用于决定哪些返回的response需要缓存, 以及获取对应的请求method
    * 示例代码:
        
        ```java
         Http.initCachePolicyHandler(this, HttpPolicyHandler.getBuilder(this).connectionTimeout(10000).socketTimeout(10000).setPolicies(null).setCache(cache).build(), new Http.Init() {
            @Override
            public String getMethodName(String url) {
                return url.replaceAll(".*&method=(\\w+?)&.*","$1");
            }
        
            @Override
            public int getResponseCode(String result) {
                if (result == null || TextUtils.isEmpty(result.trim())) {
                    return ErrorCode.ERROR_INVALID_RESPONSE;
                }
                try {
                    Type type = new TypeToken<Response<Object>>() {
                    }.getType();
                    Response<Object> response = gson.fromJson(result, type);
                    if (response == null) {
                        throw new JsonSyntaxException("Error format.");
                    }
                    return (int) response.return_code;
                } catch (final Exception e) {
                    L.e("Http", e.getMessage(), e);
                    return ErrorCode.ERROR_INVALID_RESPONSE;
                }
            }
         });
        ```
2. Cache缓存模块
    * 使用方法:
        * 建议在全局Application中启动时执行初始化方法
        * 使用com.waynezhang.mcommon.cache.Cache中的Builder类构建Cache实例对象, 支持自定义的DiskCache或者OkHttp的DiskCache, 支持自定义的图片内存缓存或者Picasso的图片缓存
    * 示例代码:
    
        ```java
        Cache cache = Cache.getBuilder(getApplicationContext()).build();
        ```
3. 公共组件
    * 使用方法: 参见相关代码注释

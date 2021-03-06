# miaoshaDemo
秒杀系统高并发学习
---
注意事项:使用GlobalExceptionHandler进行拦截异常处理,返回的result对象中传入的枚举不能直接修改
,应该在引用枚举对象的信息后再添加异常信息.

## 处理高并发的要点

1.多级缓存,包括前后端分离,浏览器对界面的缓存,后端使用redis细粒度的对对象进行缓存

2.对接口的优化处理

## 秒杀接口优化

思路:减少数据库的访问

        1系统初始化,把商品库存数量加载到Redis
        2.收到请求,Redis预减库存,库存不足,直接返回,否则进入3
        3.请求入队,立即返回队列排队中
        4.请求出队,生成订单,减少库存
        5.客户端轮询,是否秒杀成功
       
核心:

        1.Redis预减库存,减少数据库访问
        2.n内存标记减少Redis访问
        3.RabbitMQ队列缓冲,异步下单,增强用户体验

## 安全优化
        
1.秒杀接口的地址隐藏

主要逻辑:

        接口隐藏的具体流程:
        1.首先创建返回url的接口
        2.对生成的url进行redis缓存,可以使用身份和想要请求的接口表示进key值绑定
        3.对应接口的url中前端需要加上之前创建的url
        4.后端请求的url中获取对应的字符串,对redis的缓存值进行匹配
        5.匹配失败则返回非法请求
        6.匹配成功则执行后续的逻辑操作

2.数学公式验证码

3.接口的限流防刷

主要逻辑:

        通过redis缓存进行限制,例如1分钟最多进行10次请求,
        通过redis进行计数若一分钟内达到10次则不能再进行访问
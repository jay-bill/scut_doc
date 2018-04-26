# scut_doc
<h1>文档管理与查重系统</h1>
<p>该系统主要提供给某个老师检测学生doc作业相似度、重复率的，它既能管理文档，也能查重。该系统是使用springboot和一点点springcloud的知识搭建的微服务架构，主要分为三个模块：web、word、similarity。</p>
<br>
<h1>一、模块介绍：</h1>
<h2>1、web模块</h2>
<p>主要包括登录界面、用户管理、查重管理等提供
给用户使用的界面；目前又两个权限，管理员和普通用户；由于当初设计该系统时一些客观条件的限制，是专门为一个管理员设计的；如果多个管理员都添加用户，那么
会首先判断这些用户是否已经被添加到表中，如果添加了则维持现状，再将添加的用户与管理员在关系表中建立联系。如果想要支持多管理员，可以设置一个超级管理员，把
添加删除用户等操作统一由它管理，并将用户分配给各个管理员。</p>
<br/>
<h2>2、word模块</h2>
<p>主要功能为接收word模块转发的文件的地址、解析文件、分词等功能；web模块上传文件之后，无需等待后面解析、分词等耗时功能的完成，即可立即返回，即是异步的。
word模块接收文件地址之后，把它放到一个线程安全的队列里，然后用一个定时器轮询消费该队列。目前的实现是web模块上传的文件需保存到本地硬盘，所以word模块
要和web模块在一台机器上；当然也可在不同机器上，但那样需要引入一个文件服务器。这个功能以后有空再实现。</p
<br/>
<h2>3、similarity模块</h2>
<p>主要负责接收word模块的分词数组、将分词数组保存到redis数据库、计算余弦相似度等功能。读者需注意的是，本来应该在word模块分好词之后就把分词数组
存放到redis中，其中由于设计上的失误，这时候变成了similarity模块处理这些。以后再调整。</p>

<h1>二、使用方法</h1>
<p>1、下载这个工程，根据ddl.txt生成数据库表格；</p>
<p>2、安装redis，并在similarity模块的相关配置文件修改为你自己的redis服务的ip；</p>
<p>3、将三个模块导入eclipse，启动web模块，运行Application.java文件即可；其他模块同样这样运行</p>

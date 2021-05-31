--登录mysql服务器
mysql -hlocalhost -uroot -p123456

--创建数据库 imooc_coupon_data
CREATE DATABASE IF NOT EXISTS imooc_coupon_data

--登录Mysql服务器，并进入到imooc_coupon_data数据库
mysql -hlocalhost -uroot -p123456 -Dimooc_coupon_data
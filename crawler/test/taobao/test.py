from TSDK.api.taobao.h5 import TaobaoH5

client = TaobaoH5()
# 触发登录显示二维码，扫码后即可登录
loginStatus = client.qrLogin()
if not loginStatus:
    print('登录失败')
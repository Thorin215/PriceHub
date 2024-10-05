'''
运行文件输入商品关键字
起始、结束页数：表示从哪个页数开始爬取、到哪个页数结束
'''

from selenium import webdriver
from selenium.webdriver import ChromeOptions
from time import sleep
from lxml import etree
import csv

class JD_Product_Spider():
    def __init__(self):
        self.keyword = input("请输入要采集商品的关键字：")  # 商品的关键字
        option = ChromeOptions()
        option.add_experimental_option('prefs',{'profile.managed_default_content_settings.images': 2})  # 禁止图片加载，加快速度
        option.add_argument('--headless')
        option.add_argument('--disable-gpu')  # 设置无头浏览器
        self.bro = webdriver.Chrome(options=option)
        self.bro.execute_cdp_cmd("Page.addScriptToEvaluateOnNewDocument", {
            "source": """
            Object.defineProperty(navigator, 'webdriver', {
              get: () => undefined
            })
          """
        })  # 可能失效

    def Parser_Profuct_Data(self):
        html = etree.HTML(self.bro.page_source)
        li_list = html.xpath('//ul[@class="gl-warp clearfix"]/li')
        for li in li_list:
            dic = {}
            try:
                dic["title"] = li.xpath('./div/div[@class="p-name p-name-type-2"]/a/em//text()')
            except:
                dic["title"] = ""
            try:
                dic["commit"] = li.xpath('./div/div[@class="p-commit"]/strong/a/text()')[0]
            except:
                dic["commit"] = ""
            try:
                dic["shop"] = li.xpath('./div/div[@class="p-shop"]/span/a/text()')[0]
            except:
                dic["shop"] = ""
            try:
                dic["price"] = li.xpath('./div/div[@class="p-price"]/strong/i/text()')[0]
            except:
                dic["price"] = ""
            try:
                dic["details"] = "https:" + li.xpath('./div/div[@class="p-name p-name-type-2"]/a/@href')[0]
            except:
                dic["details"] = ""
            try:
                dic["productid"] =li.xpath('./@data-sku')[0]
            except:
                dic["productid"] = ""
            with open(".//jd.csv", "a+", encoding="utf-8") as f:
                writer = csv.DictWriter(f, dic.keys())
                writer.writerow(dic)

    def Get_Product_Data(self):
        self.bro.maximize_window()  # 最大化浏览器
        url = "https://search.jd.com/Search?keyword=%s" % (self.keyword)
        self.bro.get(url)
        self.bro.execute_script('window.scrollTo(0, document.body.scrollHeight)')  # 向下滑动一屏
        # 方式一：page循环访问 (推荐)
        sleep(1)
        page = self.bro.find_element_by_xpath('//span[@class="p-skip"]/em/b').text
        print("%s检索到共%s页数据"%(self.keyword,page))
        start_page=input("请输入起始页数：")
        end_page=input("请输入结束页数：")
        for i in range(int(start_page), int(end_page) + 1):
            sleep(1)
            print("-" * 30 + "已获取第%s页数据" % (i) + "-" * 30)
            url = "https://search.jd.com/Search?keyword=%s&page=%d" % (self.keyword, i * 2 - 1)
            self.bro.get(url)
            self.Parser_Profuct_Data()
        self.bro.quit()
        # 方式二：循环第几下一页 (不推荐)
        '''
            while True:
            sleep(1.2)
            self.parser_data() # 解析数据函数
            # 当到最后一页时停止循环
            try:
                if self.bro.find_element_by_xpath('//span[@class="p-num"]/a[last()]').get_attribute("class")=="pn-next disabled":
                    break
                else:
                    page = self.bro.find_element_by_xpath('//span/input[@class="input-txt"]').get_attribute("value")  # 获取当前为第几页
                    print("-" * 30 + "已获取第%s页数据" % (page) + "-" * 30)
                    self.bro.find_element_by_xpath('//span[@class="p-num"]/a[last()]').click() # 点击下一页按钮
            except:
                try:
                    self.bro.find_element_by_xpath('//span[@class="p-num"]/a[last()]').click()  # 点击下一页按钮
                except:
                    pass
        print("共获取%s页数据"%(self.bro.find_element_by_xpath('//span/input[@class="input-txt"]').get_attribute("value")))
        self.bro.quit()
        '''

if __name__ == "__main__":
    Spider=JD_Product_Spider()
    Spider.Get_Product_Data()
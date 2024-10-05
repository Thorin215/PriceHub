'''
运行文件
输入商品ID：商品ID为商品详情页url中的一部分 如：https://item.jd.com/100018642180.html 其中商品ID为：100018642180
输入起始页数结束页数
'''

import requests,csv,time
from pathos.multiprocessing import ProcessingPool as newPool

class JD_Comment_Spider():
    def Get_Comment_Data(self):
        self.ProductId=input("请输入商品ID：")
        self.start_page=input("请输入起始页数：")
        self.end_page=input("请输入结束页数：")
        pool = newPool(2)
        pool.map(self.Parser_Comment_Data,self.Params_List())

    def Params_List(self):
        params_list=[]
        for i in range(int(self.start_page),int(self.end_page)):
            params = {
                "productId": self.ProductId,
                "score": 0,
                "sortType": 5,
                "page": i,
                "pageSize": 10
            }
            params_list.append(params)
        return params_list

    def Parser_Comment_Data(self,params):
        time.sleep(3)
        head = {
            "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36"
        }
        url = "https://club.jd.com/comment/productPageComments.action?"
        text = requests.get(url=url, headers=head, params=params).json()
        data = text["comments"]
        for da in data:
            dic = {}
            dic["nickname"] = da["nickname"]
            dic["productColor"] = da["productColor"]
            dic["productSize"] = da["productSize"]
            dic["replyCount"] = da["replyCount"]
            dic["usefulVoteCount"] = da["usefulVoteCount"]
            dic["content"] = da["content"]
            dic["creationTime"] = da["creationTime"]
            with open(".//comments.csv", "a", encoding="utf-8") as f:
                writer = csv.DictWriter(f, dic.keys())
                writer.writerow(dic)


if __name__ == "__main__":
    Spider=JD_Comment_Spider()
    Spider.Get_Comment_Data()
import request from '@/utils/request'

export function queryAllGoods(){
  return request({
    url: 'http://localhost:8080/api/goods/all',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post'
  })
}
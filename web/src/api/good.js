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

export function queryGoods(params){
  return request({
    url: 'http://localhost:8080/api/goods/search',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'get',
    params
  })
}

export function updateGoods(params){
  return request({
    url: 'http://localhost:8080/api/goods/update',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    params
  })
}
import request from '@/utils/request'

export function queryAllUsers() {
  return request({
    url: 'http://localhost:8888/api/v1/user/all',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post'
  })
}

export function queryUser(data) {
  return request({
    url: 'http://localhost:8888/api/v1/user',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

export function createUser(data) {
  return request({
    url: 'http://localhost:8080/api/users',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

export function checkLogin(data) {
  return request({
    url: 'http://localhost:8888/api/v1/user/login',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}
import request from '@/utils/request'

export function queryAllUsers() {
  return request({
    url: 'http://localhost:8080/api/user/all',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post'
  })
}

export function queryUser(data) {
  return request({
    url: 'http://localhost:8080/api/user',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

export function createUser(data) {
  return request({
    url: 'http://localhost:8080/api/user/create',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

export function checkLogin(data) {
  return request({
    url: 'http://localhost:8080/api/user/login',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

export function resetPassword(data) {
  return request({
    url: 'http://localhost:8080/api/user/reset',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}

export function sendCode(email) {
  return request({
    url: `http://localhost:8080/api/user/sendver?email=${email}`,
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post'
  })
}

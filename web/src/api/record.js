import request from '@/utils/request'

export function querySearchRecordsByUser(data) {
  return request({
    url: 'http://localhost:8080/api/record/user',
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post',
    data
  })
}
import request from '@/utils/request'

export function queryAllVersions(goodId) {
  return request({
    url: `http://localhost:8080/api/versions/query/${goodId}`,
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'get'
  });
}

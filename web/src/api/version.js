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


export function queryLatestVersion(goodId) {
  return request({
    url: `http://localhost:8080/api/versions/latest/${goodId}`,  // 请求最新版本号接口
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'get'
  });
}
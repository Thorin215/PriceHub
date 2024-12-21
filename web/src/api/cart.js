import request from '@/utils/request';
import { use } from 'echarts';

/**
 * 获取用户购物车数据
 * @param {string} userId 用户 ID
 * @returns {Promise} 返回购物车数据
 */
export function getCartItems(userId) {
  return request({
    url: `http://localhost:8080/api/cart/${userId}`,
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'get'
  });
}

// /**
//  * 删除购物车中的某个商品
//  * @param {string} cartItemId 购物车商品 ID
//  * @returns {Promise} 返回删除结果
//  */
// export function removeCartItem(cartItemId) {
//   return request({
//     url: `http://localhost:8080/api/cart/remove`,
//     headers: {
//       'Content-Type': 'application/json'
//     },
//     method: 'delete',
//     data: {
//       id: cartItemId
//     }
//   });
// }

// /**
//  * 更新购物车中的某个商品
//  * @param {string} cartItemId 购物车商品 ID
//  * @param {object} updateData 更新的数据对象
//  * @returns {Promise} 返回更新结果
//  */
// export function updateCartItem(cartItemId, updateData) {
//   return request({
//     url: `http://localhost:8080/api/cart/update`,
//     headers: {
//       'Content-Type': 'application/json'
//     },
//     method: 'put',
//     data: {
//       id: cartItemId,
//       ...updateData
//     }
//   });
// }

export function addToCart(data) {
  return request({
    url: 'http://localhost:8080/api/cart/add',
    method: 'post',
    headers: {
      'Content-Type': 'application/json',
    },
    data: {
      userId: data.userId,
      versionId: data.versionId,
      goodId: data.goodId,
    },
  });
}

export function clearCart(userId){
  return request({
    url: `http://localhost:8080/api/cart/clear?userId=${userId}`,
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'delete'
  })
}

export function removeCartItem(data){
  return request({
    url: 'http://localhost:8080/api/cart/remove',
    method: 'delete',
    headers: {
      'Content-Type': 'application/json',
    },
    data: {
      userId: data.userId,
      versionId: data.versionId,
      goodId: data.goodId,
    },
  });
}


export function updateCartItem(data){
  return request({
    url: 'http://localhost:8080/api/cart/update',
    method: 'post',
    headers: {
      'Content-Type': 'application/json',
    },
    data: {
      userId: data.userId,
      versionId: data.versionId,
      goodId: data.goodId,
    },
  });
}

export function checkCart(userId){
  return request({
    url: `http://localhost:8080/api/cart/check?userId=${userId}`,
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'post'
  })
}
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/good',
    children: [
      {
        path: 'good',
        name: 'Good',
        component: () => import('@/views/good/list/index'),
        meta: {
          title: '商品信息',
          icon: 'xj'
        }
      }]
    },
    {
      path: '/',
      component: Layout,
      redirect: '/cart',
      children: [{
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/cart/all/index'),
        meta: {
          title: '我的收藏',
          icon: 'pen'
        }
      }
      ]
    }
    // ,
    // {
    //   path: '/',
    //   component: Layout,
    //   redirect: '/record',
    //   children: [{
    //     path: 'record',
    //     name: 'Record',
    //     component: () => import('@/views/record/all/index'),
    //     meta: {
    //       title: '个性化记录',
    //       icon: 'new3'
    //     }
    //   }]
    // }
]

export const asyncRoutes = [
  {
    path: '*',
    redirect: '/404',
    hidden: true
  }
]


const createRouter = () => new Router({
  base: '/',
  // mode: 'history', // require service support
  scrollBehavior: () => ({
    y: 0
  }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router

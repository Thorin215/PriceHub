<template>
  <div class="container">
    <h2 class="title">我的收藏</h2>

    <!-- 按钮组 -->
    <div class="button-group">
      <el-button type="danger" @click="clearCart">清空收藏夹</el-button>
      <el-button type="primary" @click="sendReminder">一键发送提示</el-button>
    </div>

    <el-table
      :data="cartItems"
      border
      style="width: 100%"
      v-loading="loading"
    >
      <!-- 商品图片列 -->
      <el-table-column label="商品图片" width="150">
        <template #default="scope">
          <img :src="scope.row.goodImage" alt="商品图片" style="width: 100px; height: 100px; object-fit: cover;" />
        </template>
      </el-table-column>

      <!-- 商品名称列 -->
      <el-table-column prop="goodName" label="商品名称" width="200" />

      <!-- 商品ID列 -->
      <!-- <el-table-column prop="goodId" label="商品ID" width="150" /> -->

      <!-- 版本ID列 -->
      <!-- <el-table-column prop="versionId" label="版本ID" width="150" /> -->

      <el-table-column prop="price" label="关注时价格" width="150" />

      <el-table-column prop="newprice" label="最新价格" width="150" />

      <!-- 操作列 -->
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button
            size="mini"
            type="danger"
            @click="removeItem(scope.row)"
          >删除</el-button>
          <el-button
            size="mini"
            type="primary"
            @click="updateItem(scope.row)"
          >更新</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="cartItems.length === 0" class="no-data">
      <p>收藏夹为空</p>
    </div>
  </div>
</template>

<script>
import { getCartItems, removeCartItem, updateCartItem, clearCart } from "@/api/cart";
import { checkCart } from "@/api/cart"; // 引入 checkCart API
import { mapGetters } from 'vuex';

export default {
  name: "CartPage",
  data() {
    return {
      loading: false,
      cartItems: [], // 存储购物车数据
    };
  },
  computed: {
    ...mapGetters([
      'userId',
      'roles',
      'userName'
    ])
  },
  created() {
    this.fetchCartItems();
  },
  methods: {
    // 获取购物车数据
    async fetchCartItems() {
      this.loading = true;
      try {
        const response = await getCartItems(this.userId);
        this.cartItems = response.data || response; // 根据API返回的数据格式
      } catch (error) {
        this.$message.error("加载购物车数据失败！");
      } finally {
        this.loading = false;
      }
    },

    // 删除购物车商品
    async removeItem(item) {
      try {
        await removeCartItem({
          userId: this.userId,
          goodId: item.goodId, // 从当前行数据中获取 goodId
          versionId: item.versionId // 从当前行数据中获取 versionId
        });
        this.$message.success("商品删除成功！");
        this.fetchCartItems(); // 重新加载购物车数据
      } catch (error) {
        this.$message.error("商品删除失败！");
      }
    },

    // 更新购物车商品
    async updateItem(item) {
      try {
        await updateCartItem({
          userId: this.userId,
          goodId: item.goodId, // 从当前行数据中获取 goodId
          versionId: item.versionId // 从当前行数据中获取 versionId
        });
        this.$message.success("商品更新成功！");
        this.fetchCartItems(); // 重新加载购物车数据
      } catch (error) {
        this.$message.error("商品更新失败！");
      }
    },

    // 清空购物车
    async clearCart() {
      try {
        const response = await clearCart(this.userId);
        this.$message.success("购物车已清空！");
        this.fetchCartItems(); // 重新加载购物车数据
      } catch (error) {
        this.$message.error("清空购物车失败！");
      }
    },

    // 一键发送提示
    async sendReminder() {
      try {
        const response = await checkCart(this.userId); // 调用 checkCart API
        this.$message.success("提示发送成功！");
      } catch (error) {
        this.$message.error("提示发送失败！");
      }
    }
  },
};
</script>

<style>
.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

.title {
  font-size: 1.5rem;
  margin-bottom: 20px;
  text-align: center;
}

.no-data {
  text-align: center;
  color: #999;
  font-size: 1.2rem;
  margin-top: 20px;
}

img {
  width: 100px;
  height: 100px;
  object-fit: cover;  /* 确保图片缩放时保持比例 */
}

.button-group {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
}
</style>

<template>
  <div class="container">
    <h2 class="title">我的购物车</h2>

    <el-table
      :data="cartItems"
      border
      style="width: 100%"
      v-loading="loading"
    >
      <el-table-column prop="name" label="商品名称" width="200" />
      <el-table-column prop="goodId" label="商品ID" width="150" />
      <el-table-column prop="versionId" label="版本ID" width="150" />
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
      <p>购物车为空。</p>
    </div>
  </div>
</template>

<script>
import { getCartItems, removeCartItem, updateCartItem } from "@/api/cart";
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
        this.cartItems = response;
      } catch (error) {
        this.$message.error("加载购物车数据失败！");
      } finally {
        this.loading = false;
      }
    },

    // 删除购物车商品
    async removeItem(item) {
      try {
        await removeCartItem(item.id);
        this.$message.success("商品删除成功！");
        this.fetchCartItems(); // 重新加载购物车数据
      } catch (error) {
        this.$message.error("商品删除失败！");
      }
    },

    // 更新购物车商品（可扩展逻辑）
    async updateItem(item) {
      try {
        await updateCartItem(item.id, { versionId: item.versionId + 1 });
        this.$message.success("商品更新成功！");
        this.fetchCartItems(); // 重新加载购物车数据
      } catch (error) {
        this.$message.error("商品更新失败！");
      }
    },
  },
};
</script>

<style>
.container {
  max-width: 800px;
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
</style>

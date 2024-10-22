<template>
  <div class="container">
    <el-button type="primary" @click="showProductList">显示商品列表</el-button>

    <div v-if="showProducts" class="product-list">
      <h1>商品列表</h1>
      <el-card
        v-for="product in products"
        :key="product.id" <!-- 修改为使用 id 作为唯一键 -->
        class="product-card"
      >
        <h4 class="product-name">{{ product.name || '无' }}</h4>
        <p class="product-description">描述: {{ product.description || '无' }}</p> <!-- 添加描述 -->
        <p class="product-category">类别: {{ product.category || '无' }}</p> <!-- 添加类别 -->
        <div class="product-actions">
          <el-button type="info" icon="el-icon-info" @click="viewDetails(product)">查看详细信息</el-button>
          <el-button type="success" icon="el-icon-shopping-cart" @click="addToCart(product)">加入购物车</el-button>
        </div>
      </el-card>
    </div>

    <el-dialog title="商品详细信息" :visible.sync="detailsDialogVisible" width="60%" @close="closeDetailsDialog">
      <el-form :model="selectedProduct" label-width="120px">
        <el-form-item label="商品名称">
          <div>{{ selectedProduct.name || '无' }}</div>
        </el-form-item>
        <el-form-item label="描述">
          <div>{{ selectedProduct.description || '无' }}</div>
        </el-form-item>
        <el-form-item label="类别">
          <div>{{ selectedProduct.category || '无' }}</div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeDetailsDialog">返回</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import { queryAllGoods } from '@/api/good';

export default {
  name: 'ProductsTable',
  data() {
    return {
      showProducts: false,
      products: [],
      selectedProduct: {
        name: '',
        description: '',
        category: '',
      },
      detailsDialogVisible: false,
    };
  },
  methods: {
    showProductList() {
      this.showProducts = true; // 点击按钮后显示商品列表
      this.fetchProducts(); // 获取商品数据
    },
    async fetchProducts() {
      try {
        const response = await queryAllGoods();
        this.products = response; // 更新产品数据
      } catch (error) {
        this.$message.error('获取商品列表失败');
      }
    },
    viewDetails(product) {
      this.selectedProduct = product;
      this.detailsDialogVisible = true;
    },
    closeDetailsDialog() {
      this.detailsDialogVisible = false;
    },
    addToCart(product) {
      this.$message.success(`${product.name} 已加入购物车！`);
    }
  }
};
</script>
<style>
.container {
  padding: 40px;
  background-color: #f9f9f9; /* 添加背景色 */
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 5px; /* 商品之间的间距 */
}

.product-card {
  width: 40%;
  border: 1px solid #e0e0e0; /* 添加边框 */
  border-radius: 8px; /* 圆角 */
  padding: 20px; /* 内边距 */
  transition: box-shadow 0.3s ease; /* 动画效果 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 阴影效果 */
}

.product-card:hover {
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2); /* 悬停时的阴影效果 */
}

.product-name {
  font-size: 1.5em; /* 增加字体大小 */
  color: #333; /* 字体颜色 */
}

.product-price {
  font-size: 1.2em; /* 增加字体大小 */
  color: #ff5722; /* 使用醒目的颜色 */
}

.product-stock {
  color: #666; /* 使用灰色文字 */
}

.product-actions {
  display: flex;
  justify-content: space-between; /* 按钮之间的间距 */
  margin-top: 10px; /* 上边距 */
}

.el-button {
  transition: background-color 0.3s ease; /* 按钮颜色变化动画 */
}

.el-button:hover {
  background-color: #0056b3; /* 悬停时按钮颜色变化 */
}

.dialog-footer {
  display: flex;
  justify-content: flex-end; /* 按钮靠右 */
}
</style>

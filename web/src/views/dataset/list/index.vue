<template>
  <div class="container">
    <!-- 数据集创建表单 -->
    <el-button type="primary" @click="showProductList">显示商品列表</el-button>

    <div v-if="showProducts" class="product-list">
      <h1>商品列表</h1>
      <el-card
        v-for="product in products"
        :key="product.name"
        class="product-card"
      >
        <h4 class="product-name">{{ product.name }}</h4>
        <p class="product-price">价格: {{ product.price }} 元</p>
        <p class="product-stock">库存: {{ product.stock }}</p>
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
        <el-form-item label="价格">
          <div>{{ selectedProduct.price || '无' }} 元</div>
        </el-form-item>
        <el-form-item label="库存">
          <div>{{ selectedProduct.stock || '无' }}</div>
        </el-form-item>
        <el-form-item label="描述">
          <div>{{ selectedProduct.description || '无' }}</div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeDetailsDialog">返回</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: 'ProductsTable',
  data() {
    return {
      showProducts: false, // 控制商品列表的显示状态
      products: [
        { name: 'iPhone 12', price: 6999, stock: 10, description: '苹果手机' },
        { name: '三星电视', price: 3999, stock: 5, description: '高清电视' },
        { name: '索尼耳机', price: 999, stock: 20, description: '降噪耳机' }
      ],
      selectedProduct: {
      name: '',
      price: 0,
      stock: 0,
      description: '',
    },
      detailsDialogVisible: false,
    };
  },
  methods: {
    showProductList() {
      this.showProducts = true; // 点击按钮后显示商品列表
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
  padding: 100px;
  background-color: #f9f9f9; /* 添加背景色 */
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 20px; /* 商品之间的间距 */
}

.product-card {
  width: 100%;
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

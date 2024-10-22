<template> 
  <div class="container">
    <el-button type="primary" @click="showProductList">显示商品列表</el-button>

    <div v-if="showProducts" class="product-list">
      <h1>商品列表</h1>
      <el-card
        v-for="product in products"
        :key="product.id" 
        class="product-card"
      >
        <h4 class="product-name">{{ product.name || '无' }}</h4>
        <p class="product-description">描述: {{ product.description || '无' }}</p>
        <p class="product-category">类别: {{ product.category || '无' }}</p>
        <div class="product-actions">
          <el-button type="info" icon="el-icon-info" @click="viewDetails(product)">查看详细信息</el-button>
          <el-button type="success" icon="el-icon-shopping-cart" @click="addToCart(product)">加入购物车</el-button>
        </div>
      </el-card>
    </div>

    <el-dialog title="商品价格变化" :visible.sync="detailsDialogVisible" width="60%" @close="closeDetailsDialog">
      <el-table :data="priceData" style="width: 100%">
        <el-table-column prop="createdAt" label="时间" :formatter="formatDate" class-name="blue-text" />
        <el-table-column prop="price" label="价格" />
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeDetailsDialog">返回</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { queryAllGoods } from '@/api/good';
import { queryAllVersions } from '@/api/version';

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
      priceData: [],
    };
  },
  methods: {
    showProductList() {
      this.showProducts = true;
      this.fetchProducts();
    },
    async fetchProducts() {
      try {
        const response = await queryAllGoods();
        this.products = response;
        this.$message.success('获取商品列表成功');
      } catch (error) {
        this.$message.error('获取商品列表失败');
      }
    },
    async viewDetails(product) {
      this.selectedProduct = product;
      await this.fetchVersions(product.id);
      this.detailsDialogVisible = true;
    },
    async fetchVersions(goodId) {
      try {
        const response = await queryAllVersions(goodId);
        this.priceData = response; // 假设返回的价格数据包含时间戳和价格
      } catch (error) {
        this.$message.error('获取版本数据失败');
      }
    },
    closeDetailsDialog() {
      this.detailsDialogVisible = false;
      this.priceData = []; // 清空数据
    },
    addToCart(product) {
      this.$message.success(`${product.name} 已加入购物车！`);
    },
    formatDate(row) {
    const date = new Date(row.createdAt);
    return date.toLocaleString(); // 格式化为本地时间字符串
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

.blue-text {
  color: rgb(43, 168, 240); /* 设置文本颜色为蓝色 */
}

.product-card:hover {
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2); /* 悬停时的阴影效果 */
}

.product-name {
  font-size: 1.5em; /* 增加字体大小 */
  color: #333; /* 字体颜色 */
}

.product-actions {
  display: flex;
  justify-content: space-between; /* 按钮之间的间距 */
  margin-top: 10px; /* 上边距 */
}

.dialog-footer {
  display: flex;
  justify-content: flex-end; /* 按钮靠右 */
}
</style>


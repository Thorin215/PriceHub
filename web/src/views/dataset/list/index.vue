<template>
  <div class="container">
    <!-- 搜索框 -->
    <div class="search-box">
      <el-input
        v-model="parentCategory"
        placeholder="输入父类别"
        class="search-input"
      ></el-input>
      <el-input
        v-model="childCategory"
        placeholder="输入子类别"
        class="search-input"
      ></el-input>
      <el-button type="primary" @click="filterProducts">搜索</el-button>
    </div>

    <!-- 商品列表 -->
    <div v-if="showProducts" class="product-list">
      <h1>商品列表</h1>
      <el-card
        v-for="product in filteredProducts"
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

    <!-- 详细信息对话框 -->
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
      showProducts: true,
      products: [],
      filteredProducts: [], // 存放筛选后的商品列表
      parentCategory: '', // 父类别输入框的值
      childCategory: '',  // 子类别输入框的值
      selectedProduct: {
        name: '',
        description: '',
        category: '',
      },
      detailsDialogVisible: false,
      priceData: [],
    };
  },
  mounted() {
    this.fetchProducts(); // 页面加载时获取商品列表
  },
  methods: {
    async fetchProducts() {
      try {
        const response = await queryAllGoods();
        this.products = response;
        this.filteredProducts = response; // 初始显示所有商品
        this.$message.success('获取商品列表成功');
      } catch (error) {
        this.$message.error('获取商品列表失败');
      }
    },
    filterProducts() {
      // 根据父类别和子类别输入框的值筛选商品
      this.filteredProducts = this.products.filter(product => {
        const parentMatch = this.parentCategory
          ? product.parentCategory && product.parentCategory.includes(this.parentCategory)
          : true;
        const childMatch = this.childCategory
          ? product.category && product.category.includes(this.childCategory)
          : true;
        return parentMatch && childMatch;
      });
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
  background-color: #f9f9f9;
}

.search-box {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  width: 200px;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.product-card {
  width: 40%;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  transition: box-shadow 0.3s ease;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.product-name {
  font-size: 1.5em;
  color: #333;
}

.product-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>



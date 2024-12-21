<template>
  <div class="container">
    <!-- 搜索框 -->
    <div class="search-box">
      <el-input
        v-model="productName"
        placeholder="输入商品名称"
        class="search-input"
      ></el-input>
      <el-select v-model="selectedPlatform" placeholder="选择平台" @change="filterProducts">
        <el-option label="阿里巴巴" value="阿里巴巴"></el-option>
        <el-option label="苏宁" value="苏宁"></el-option>
      </el-select>
      <el-button type="primary" @click="filterProducts">搜索</el-button>
      <el-button type="success" @click="updateGood">更新</el-button>
    </div>

    <hr class="divider" />

    <!-- 商品列表 -->
    <div v-if="showProducts" class="product-list">
      <h1>商品列表</h1>
      <div class="product-grid">
        <el-card
          v-for="product in filteredProducts"
          :key="product.id"
          class="product-card"
        >
          <div class="product-content">
            <!-- 图片在文字上 -->
            <img :src="product.image" alt="商品图片" class="product-image" />
            <h4 class="product-name">{{ product.name || '无' }}</h4>
            <el-tag :class="productPlatformClass(product.platform)" effect="dark">{{ product.platform }}</el-tag>
            <p class="product-description">描述: {{ product.description || '无' }}</p>
            <div class="product-actions">
              <el-button type="info" icon="el-icon-info" @click="viewDetails(product)">查看价格变化</el-button>
              <el-button type="success" icon="el-icon-shopping-cart" @click="addToCart(product)">加入收藏夹</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 详细信息对话框 -->
    <el-dialog title="商品价格变化" :visible.sync="detailsDialogVisible" width="60%" @close="closeDetailsDialog">
      <div id="price-chart" style="width: 100%; height: 400px;"></div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeDetailsDialog">返回</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import { queryAllGoods, queryGoods, updateGoods } from '@/api/good';
import { queryAllVersions, queryLatestVersion } from '@/api/version';
import { addToCart } from '@/api/cart';
import { mapGetters } from 'vuex';

export default {
  name: 'ProductsTable',
  data() {
    return {
      showProducts: true,
      products: [],
      filteredProducts: [], // 存放筛选后的商品列表
      productName: '',     // 商品名称输入框的值
      selectedProduct: {
        name: '',
        description: '',
        category: '',
      },
      detailsDialogVisible: false,
      priceData: [],
      chartInstance: null, // 保存 ECharts 实例
      selectedPlatform: '', // 选择的平台
    };
  },
  computed: {
    ...mapGetters([
      'userId',
      'roles',
      'userName'
    ])
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
    async filterProducts() {
      if (this.productName === '' && this.selectedPlatform === '') {
        this.filteredProducts = this.products;
        this.$message.success('筛选商品列表成功');
      } else {
        try {
          const response = await queryGoods({ name: this.productName, platform: this.selectedPlatform }); // 确认 queryGoods 的参数结构是否正确
          if (this.selectedPlatform === '') {
            this.filteredProducts = response;
          } else {
            this.filteredProducts = response.filter(product => product.platform === this.selectedPlatform);
          }
          // this.filteredProducts = response;
          this.$message.success('获取商品列表成功');
        } catch (error) {
          this.$message.error('获取商品列表失败');
        }
      }
    },
    async updateGood() {
      if (this.productName === '') {
        this.$message.info('请输入商品名称');
        return;
      } else {
        try {
          const response = await updateGoods({ name: this.productName });
          this.$message.success('更新商品列表成功');
          this.fetchProducts();
        } catch (error) {
          this.$message.error('更新商品列表失败');
        }
      }
    },
    async viewDetails(product) {
      this.selectedProduct = product;
      await this.fetchVersions(product.id);
      this.detailsDialogVisible = true;
      this.$nextTick(() => {
        this.initChart();
      });
    },
    async fetchVersions(goodId) {
      try {
        const response = await queryAllVersions(goodId);
        this.priceData = response.map(item => ({
          time: item.createdAt,
          price: item.price,
        })); // 格式化为时间和价格
      } catch (error) {
        this.$message.error('获取版本数据失败');
      }
    },
    initChart() {
      if (!this.chartInstance) {
        this.chartInstance = echarts.init(document.getElementById('price-chart'));
      }

      const times = this.priceData.map(item => new Date(item.time).toLocaleDateString());
      const prices = this.priceData.map(item => item.price);

      const option = {
        title: {
          text: '商品价格变化趋势',
          left: 'center',
        },
        tooltip: {
          trigger: 'axis',
        },
        xAxis: {
          type: 'category',
          data: times,
          boundaryGap: false,
          axisLabel: {
            rotate: 45,
          },
        },
        yAxis: {
          type: 'value',
          name: '价格 (元)',
        },
        series: [
          {
            data: prices,
            type: 'line',
            smooth: true,
            areaStyle: {},
          },
        ],
      };

      this.chartInstance.setOption(option);
    },
    closeDetailsDialog() {
      this.detailsDialogVisible = false;
      this.priceData = [];
      if (this.chartInstance) {
        this.chartInstance.dispose();
        this.chartInstance = null;
      }
    },
    async addToCart(product) {
      const response = await queryLatestVersion(product.id);
      const res_cart = await addToCart({userId: this.userId, versionId: response.id, goodId: product.id});
      this.$message.success(res_cart);
    },
    formatDate(row) {
      const date = new Date(row.createdAt);
      return date.toLocaleString(); // 格式化为本地时间字符串
    },
    productPlatformClass(platform) {
      return platform === '阿里巴巴' ? 'alibaba-tag' : 'suning-tag';
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
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  width: 200px;
}

.divider {
  margin: 20px 0;
  border: 0;
  height: 1px;
  background-color: #e0e0e0;
}

.product-list h1 {
  margin-bottom: 10px;
  font-size: 1.8em;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.product-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  transition: box-shadow 0.3s ease;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.product-card:hover {
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.product-content {
  text-align: center;
}

.product-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 10px;
}

.product-name {
  font-size: 1.5em;
  color: #333;
  margin: 10px 0;
}

.product-platform {
  margin-bottom: 10px;
}

.product-description,
.product-category {
  margin: 5px 0;
  color: #555;
}

.product-actions {
  display: flex;
  justify-content: space-around;
  margin-top: 10px;
  width: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

.alibaba-tag {
  background-color: orange;
  color: white;
}

.suning-tag {
  background-color: yellow;
  color: black;
}
</style>
<template>
  <div>
    <el-table :data="searchRecords" style="width: 100%">
      <el-table-column prop="userId" label="User ID" width="180">
      </el-table-column>
      <el-table-column prop="productName" label="Product Name" width="180">
      </el-table-column>
      <el-table-column prop="searchCount" label="Search Count" width="180">
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import { querySearchRecordsByUser } from '@/api/record';

export default {
  data() {
    return {
      searchRecords: [] // 存储搜索记录
    };
  },
  computed: {
    ...mapGetters(['userId', 'userName', 'roles']),
  },
  mounted() {
    this.fetchSearchRecords(); // 组件挂载时获取搜索记录
  },
  methods: {
    async fetchSearchRecords() {
      try {
        const response = await querySearchRecordsByUser({ userId: this.userId }); // 调用 API 获取搜索记录
        this.searchRecords = response.data; // 假设返回的数据在 data 字段中
      } catch (error) {
        this.$message.error("Failed to fetch search records");
      }
    }
  }
};
</script>
<template>
  <el-scrollbar height="100%" style="width: 100%;">
    <div style="margin-top: 20px; margin-left: 40px; font-size: 2em; font-weight: bold;">
      图书管理 <!-- 页面标题 -->
      <el-input v-model="toSearch" :prefix-icon="Search"
                style="width: 15vw; min-width: 150px; margin-left: 30px; margin-right: 30px; float: right;"
                clearable />
                <el-button type="success" @click="borrowBookDialogVisible = true" style="float: right; margin-right: 10px;">
                  借书
                </el-button>

                <el-button type="warning" @click="returnBookDialogVisible = true" style="float: right; margin-right: 10px;">
                  还书
                </el-button>

                <el-button type="primary" @click="modifyStockDialogVisible = true" style="float: right; margin-right: 10px;">
                  修改库存
                </el-button>

                <el-button type="info" @click="showSearchDialog" style="float: right; margin-right: 10px;">
                  模糊查询
                </el-button>

                <el-button type="danger" @click="importDialogVisible = true">批量导入(EXCEL)</el-button>

                <el-button type="warning" @click="importDialogVisible2 = true">批量导入(路径)</el-button>

    </div>

    <div style="display: flex; flex-wrap: wrap; justify-content: start;">

      <!-- 已有书籍容器 -->
      <div class="cardBox" v-for="book in books" v-show="book.title.includes(toSearch)" :key="book.book_id">
        <div>
          <!-- 书籍详情 -->
          <div style="font-size: 12px; font-weight: bold;">{{ book.title }}</div>
          <el-divider />
          <div style="margin-left: 8px; text-align: start; font-size: 10px;">
            <p style="padding: 2.5px;"><span style="font-weight: bold;">ID：</span>{{ book.book_id }}</p>
            <p style="padding: 2.5px;"><span style="font-weight: bold;">作者：</span>{{ book.author }}</p>
            <p style="padding: 2.5px;"><span style="font-weight: bold;">类别：</span>{{ book.category }}</p>
            <p style="padding: 2.5px;"><span style="font-weight: bold;">出版社：</span>{{ book.press }}</p>
            <p style="padding: 2.5px;"><span style="font-weight: bold;">出版年份：</span>{{ book.publish_year }}</p>
            <p style="padding: 2.5px;"><span style="font-weight: bold;">价格：</span>{{ book.price }}</p>
            <p style="padding: 2.5px;"><span style="font-weight: bold;">余量：</span>{{ book.stock }}</p>
          </div>
          <el-divider />
          <!-- 编辑和删除按钮 -->
          <div style="margin-top: -25px;">
            <!-- <el-button type="primary" :icon="Edit" circle 
             @click="editBook(book), this.editBookVisible = true" /> -->
            <!-- <el-button type="primary" :icon="Edit" circle 
             @click="editBookVisible = true; editBook(book)" /> -->
             <el-button type="primary" :icon="Edit" @click="setModifyBookInfo(book); this.editBookVisible = true" circle />
            <el-button type="danger" :icon="Delete" circle @click="this.toRemove = book.book_id, this.removeBookVisible = true" style="margin-left: 30px;" />
          </div>
        </div>
      </div>

      <el-dialog v-model="importDialogVisible" title="批量导入" width="50%">
        <!-- <el-form label-position="top">
          <el-form-item label="选择文件">
            <el-upload
              class="upload-demo"
              action="/book/import"
              :before-upload="handleBeforeUpload"
              :on-success="handleImportSuccess"
              :on-error="handleImportError"
              :file-list="importFileList"
              accept=".txt"
              multiple
            >
              <el-button size="small" type="primary">点击上传</el-button>
              <div slot="tip" class="el-upload__tip">只能上传 .txt 文件</div>
            </el-upload>
          </el-form-item>
        </el-form> -->
        
        <el-form @submit.native.prevent>
          <el-form-item label="选择 Excel 文件">
            <el-upload
              accept=".xlsx, .xls"
              action="#"
              :show-file-list="false"
              :on-change="handleExcelUpload"
            >
              <el-button type="primary">选择文件</el-button>
            </el-upload>
            <div v-if="fileError" style="color: red;">{{ fileError }}</div>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="importDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitImport" :disabled="!workbook">确定</el-button>
        </div>
      </el-dialog>

      <el-dialog title="批量导入" v-model="importDialogVisible2" width="30%">
      <el-input v-model="importPath" placeholder="请输入文件路径"></el-input>
      <span slot="footer" class="dialog-footer">
        <el-button @click="importDialogVisible2 = false">取消</el-button>
        <el-button type="primary" @click="handleImport">确定</el-button>
      </span>
    </el-dialog>

      <!-- <el-dialog
      title="批量导入图书信息"
      :v-model="importDialogVisible"
      width="30%"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form @submit.native.prevent>
        <el-form-item label="选择 Excel 文件">
          <el-upload
            accept=".xlsx, .xls"
            action="#"
            :show-file-list="false"
            :on-change="handleExcelUpload"
          >
            <el-button type="primary">选择文件</el-button>
          </el-upload>
          <div v-if="fileError" style="color: red;">{{ fileError }}</div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelImport">取消</el-button>
        <el-button type="primary" @click="submitImport" :disabled="!workbook">确定</el-button>
      </div>
    </el-dialog> -->

      <el-dialog v-model="removeBookVisible" title="删除书籍" width="30%">
        <span>确定删除<span style="font-weight: bold;">{{ toRemove }}号图书</span>吗？</span>

        <template #footer>
            <span class="dialog-footer">
                <el-button @click="removeBookVisible = false">取消</el-button>
                <el-button type="danger" @click="ConfirmRemoveBook">
                    删除
                </el-button>
            </span>
        </template>
    </el-dialog>

        <!-- 修改库存对话框 -->
        <el-dialog v-model="modifyStockDialogVisible" title="修改库存" width="30%">
          <el-form :model="modifyStockForm" label-width="100px">
            <el-form-item label="图书 ID" prop="bookId">
              <el-input v-model="modifyStockForm.bookId" clearable />
            </el-form-item>
            <el-form-item label="增减数量" prop="quantity">
              <el-input v-model="modifyStockForm.quantity" clearable />
            </el-form-item>
          </el-form>
          <template #footer>
            <span>
              <el-button @click="modifyStockDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="modifyStock(modifyStockForm); modifyStockDialogVisible = false":disabled="modifyStockForm.bookId.length === 0 || modifyStockForm.quantity.length === 0">确定</el-button>
            </span>
          </template>
        </el-dialog>
    

      <!-- 新书容器，带有加号图标 -->
      <el-button class="newCardBox" @click="showNewBookDialog">
        <el-icon style="height: 50px; width: 50px;">
          <Plus style="height: 100%; width: 100%;" />
        </el-icon>
      </el-button>

    </div>

    <!-- 借书对话框 -->
    <el-dialog v-model="borrowBookDialogVisible" title="借书" width="30%">
      <el-form :model="borrowBookForm" label-width="100px">
        <el-form-item label="借书证号" prop="borrowerId">
          <el-input v-model="borrowBookForm.borrowerId" clearable />
        </el-form-item>
        <el-form-item label="图书 ID" prop="bookId">
          <el-input v-model="borrowBookForm.bookId" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <span>
          <el-button @click="borrowBookDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="borrowBook(this.borrowBookForm); borrowBookDialogVisible = false":disabled="borrowBookForm.bookId.length === 0 || borrowBookForm.borrowerId.length === 0">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新建图书对话框 -->
    <el-dialog v-model="newBookVisible" title="新建图书" width="30%" align-center>
      <el-form :model="newBookForm" label-width="80px">
        <el-form-item label="书名" prop="title">
          <el-input v-model="newBookForm.title" clearable />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="newBookForm.author" clearable />
        </el-form-item>
        <el-form-item label="类别" prop="category">
          <el-input v-model="newBookForm.category" clearable />
        </el-form-item>
        <el-form-item label="出版社" prop="press">
          <el-input v-model="newBookForm.press" clearable />
        </el-form-item>
        <el-form-item label="出版年份" prop="publish_year">
          <el-input v-model="newBookForm.publish_year" clearable />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input v-model="newBookForm.price" clearable />
        </el-form-item>
        <el-form-item label="余量" prop="stock">
          <el-input v-model="newBookForm.stock" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <span>
          <el-button @click="newBookVisible = false">取消</el-button>
          <el-button type="primary" @click="addBook" :disabled="newBookForm.title.length === 0 || newBookForm.category.length === 0 || newBookForm.press.length === 0 || newBookForm.publish_year.length === 0 || newBookForm.author.length === 0">确定</el-button>
        </span>
      </template>
    </el-dialog>


    <el-dialog v-model="returnBookDialogVisible" title="还书" width="30%">
      <el-form :model="returnBookForm" label-width="100px">
        <el-form-item label="借书证号" prop="borrowerId">
          <el-input v-model="returnBookForm.borrowerId" clearable />
        </el-form-item>
        <el-form-item label="图书 ID" prop="bookId">
          <el-input v-model="returnBookForm.bookId" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <span>
          <el-button @click="returnBookDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="returnBook(returnBookForm); returnBookDialogVisible = false":disabled="returnBookForm.bookId.length === 0 || returnBookForm.borrowerId.length === 0">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="editBookVisible" title="修改图书" width="30%" align-center>
      <el-form :model="selectedBook" label-width="80px">
        <!-- <el-form-item label="ID" prop="title">
          <el-input v-model="selectedBook.book_id" clearable />
        </el-form-item> -->
        <el-form-item label="书名" prop="title">
          <el-input v-model="selectedBook.title" clearable />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="selectedBook.author" clearable />
        </el-form-item>
        <el-form-item label="类别" prop="category">
          <el-input v-model="selectedBook.category" clearable />
        </el-form-item>
        <el-form-item label="出版社" prop="press">
          <el-input v-model="selectedBook.press" clearable />
        </el-form-item>
        <el-form-item label="出版年份" prop="publish_year">
          <el-input v-model="selectedBook.publish_year" clearable />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input v-model="selectedBook.price" clearable />
        </el-form-item>
        <!-- <el-form-item label="余量" prop="stock">
          <el-input v-model="selectedBook.stock" clearable />
        </el-form-item> -->
      </el-form>
      <template #footer>
        <span>
          <el-button @click="editBookVisible = false">取消</el-button>
          <el-button type="primary" @click="editBook(this.selectedBook); editBookVisible = false":disabled="selectedBook.title.length === 0 || selectedBook.category.length === 0 || selectedBook.press.length === 0 || selectedBook.publish_year.length === 0 || selectedBook.author.length === 0">确定</el-button>
        </span>
      </template>
    </el-dialog>

<!-- 模糊查询 -->
    <el-dialog v-model="searchDialogVisible" title="模糊查询" width="50%">
      <el-form :model="searchQuery" label-width="100px">
        <el-form-item label="类别">
          <el-input v-model="searchQuery.category" clearable />
        </el-form-item>
        <el-form-item label="书名">
          <el-input v-model="searchQuery.title" clearable />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="searchQuery.press" clearable />
        </el-form-item>
        <el-form-item label="最小出版年份">
          <el-input v-model="searchQuery.minPublishYear" clearable />
        </el-form-item>
        <el-form-item label="最大出版年份">
          <el-input v-model="searchQuery.maxPublishYear" clearable />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="searchQuery.author" clearable />
        </el-form-item>
        <el-form-item label="最低价格">
          <el-input v-model="searchQuery.minPrice" clearable />
        </el-form-item>
        <el-form-item label="最高价格">
          <el-input v-model="searchQuery.maxPrice" clearable />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="searchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="performSearch">确定</el-button>
      </span>
    </el-dialog>
    

  </el-scrollbar>
</template>

<script>
import { Plus, Delete, Edit, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import * as XLSX from 'xlsx';


export default {
  data() {
    return {
      books: [],
      importDialogVisible: false, // 控制批量导入对话框的显示与隐藏
     importFilePath: '', // 用于存储导入文件的路径
     importPath: '', // 用于存储导入文件的路径
      newBookVisible: false,
      editBookVisible: false,
      removeBookVisible: false,
      toRemove : '',
      borrowBookDialogVisible: false,
      returnBookDialogVisible: false,
      modifyStockDialogVisible: false,
      modifyStockDialogVisible: false,
      searchDialogVisible: false,
      importDialogVisible: false, // 控制批量导入对话框的显示与隐藏
      importDialogVisible2: false, // 控制批量导入对话框的显示与隐藏
      workbook: null, // 存储 Excel 文件的 workbook 对象
      fileError: '' ,// 文件类型错误提示信息
    returnBookForm: {
      borrowerId: '',
      bookId: ''
    },
    searchQuery: {
        category: '',
        title: '',
        press: '',
        minPublishYear: '',
        maxPublishYear: '',
        author: '',
        minPrice: '',
        maxPrice: ''
      },
    borrowBookForm: {
      borrowerId: '',
      bookId: ''
    },
      selectedBook: {}, // 添加 selectedBook 对象初始化
      newBookForm: {
        book_id: '',
        title: '',
        author: '',
        category: '',
        press: '',
        publish_year: '',
        price: '',
        stock: ''
      },
      modifyStockForm: {
        bookId: '',
        quantity: ''
      },
      toSearch: '',
      Search,
      Delete,
      Edit
    }
  },
  methods: {
    showNewBookDialog() {
      this.newBookForm = { book_id: '', title: '', author: '', category: '', press: '', publish_year: '', price: '', stock: '' }
      this.newBookVisible = true
    },
    setModifyBookInfo(book) {
    // 设置选中书籍的信息到selectedBook中
    this.selectedBook = {
      book_id: book.book_id,
      title: book.title,
      author: book.author,
      category: book.category,
      press: book.press,
      publish_year: book.publish_year,
      price: book.price
      //stock: book.stock
    };
    // 显示修改对话框
    //this.editBookVisible = true;
  },
  showSearchDialog() {
      this.searchDialogVisible = true;
    },
    async performSearch() {
      
        // .then(response => {
        //   this.searchDialogVisible = false
        //   this.fetchBooks()
        //   ElMessage.success('图书查询成功')
        // })


        this.books = []; // 清空列表
    
        let response = await axios.post('/book/search', this.searchQuery); // 向 /borrow 发出 POST 请求，请求体包含 cardID=this.toQuery
        let inbooks = response.data; // 获取响应数据
        inbooks.forEach(book => { // 对于每一个借书记录
            this.books.push(book); // 将其加入到列表中
        });
        //this.isShow = true; // 显示结果列表
   
    // catch (error) {
    //     console.error('Error querying borrows:', error); // 处理错误
    //     // 可以添加适当的错误处理逻辑，例如显示错误消息给用户
    // }
    },
    addBook() {
      axios.post('/book', this.newBookForm)
        .then(response => {
          this.newBookVisible = false
          //this.fetchBooks()
          ElMessage.success('图书添加成功')
          this.fetchBooks()
        })
        .catch(error => {
          console.error('Error adding book:', error)
          ElMessage.error('图书添加失败')
        })
    },
    modifyStock(modifyStockForm) {
      axios.post("/book/modifyStock", modifyStockForm)
        .then(response => {
          if(response.data === "Stock increased successfully"){
            ElMessage.success('库存修改成功');
          this.fetchBooks();
          }
        
          else 
          ElMessage.error('库存修改失败');
          
        })
        //.catch(error => {
        //  console.error('Error modifying stock:', error)
        //  
        //})
    },
    fetchBooks() {
      axios.get('/book')
        .then(response => {
          this.books = response.data
        })
        .catch(error => {
          console.error('Error fetching books:', error)
        })
    },
    borrowBook(book) {
      axios.post("/book/borrow", book)
      .then(response => {
        if(response.data === "Book borrowed successfully")  
            ElMessage.success('图书借阅成功');
        else 
        ElMessage.error('图书借阅失败');
        })
        //.catch(error => {
         // console.error('Error borrowing book:', error)
        //  
       // })
    },
    returnBook(book) {
    axios.post("/book/return", book)
      .then(response => {
        if(response.data === "Return Failed!")
         ElMessage.error('图书归还失败');
      else
        ElMessage.success('图书归还成功');
      })
      // .catch(error => {
      //   console.error('Error returning book:', error)
        
      // })
  },
    ConfirmRemoveBook() {
      //axios.delete(`/book/${bookId}`)
      axios.post("/book/remove", {
                book_id: this.toRemove
            })
        .then(response => {
        
          this.fetchBooks()
          ElMessage.success('图书删除成功')
        })
        .catch(error => {
          console.error('Error removing book:', error)
          ElMessage.error('图书删除失败')
        })
    },
    handleImport() {
      axios.post('/book/import', {
        path: this.importPath
      })
      .then(response => {
        if(response.data === "Books stored successfully"){
          this.importDialogVisible2 = false;
        this.fetchBooks();
        ElMessage.success('图书导入成功');
        }else {
          ElMessage.error('图书导入失败');
        }

      })
      // .catch(error => {
      //   console.error('Error importing books:', error)
      //   
      // })
    },
    editBook(book) {
            axios.post("/book/modify", book)
            .then(response => {
                ElMessage.success("图书信息修改成功");
                this.modifyCardVisible = false;
                this.QueryCards();
            })
            .catch(error => {
                console.error("修改图书信息失败:", error);
                //ElMessage.error("图书信息修改失败，请稍后重试");
            });
    }
  },
  // handleExcelUpload(event) {
  //     const file = event.target.files[0];
  //     // 检查文件类型是否为 Excel
  //     if (!this.isValidExcelFile(file)) {
  //       this.fileError = '请选择 Excel 文件';
  //       return;
  //     }
  //     this.fileError = '';
  //     // 读取 Excel 文件
  //     const reader = new FileReader();
  //     reader.onload = (e) => {
  //       const data = new Uint8Array(e.target.result);
  //       const workbook = XLSX.read(data, { type: 'array' });
  //       // 存储 workbook 对象
  //       this.workbook = workbook;
  //     };
  //     reader.readAsArrayBuffer(file);
  //   },

  handleExcelUpload(event) {
  const file = event.target.files[0];
  // 检查文件类型是否为 Excel
  if (!this.isValidExcelFile(file)) {
    this.fileError = '请选择 Excel 文件';
    return;
  }
  this.fileError = '';
  // 读取 Excel 文件
  const reader = new FileReader();
  reader.onload = (e) => {
    const data = new Uint8Array(e.target.result);
    const workbook = XLSX.read(data, { type: 'array' });
    // 存储 workbook 对象
    this.workbook = workbook;
    console.log(workbook);
  };
  reader.readAsArrayBuffer(file);
},
    // 验证文件是否为 Excel
    isValidExcelFile(file) {
      return /\.(xlsx|xls)$/.test(file.name.toLowerCase());
    },
    // 提交批量导入
    // submitImport() {
    //   if (this.workbook) {
    //     // 处理 Excel 数据
    //     const firstSheetName = this.workbook.SheetNames[0];
    //     const worksheet = this.workbook.Sheets[firstSheetName];
    //     // 将 Excel 数据转换为 JSON 格式
    //     const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
    //     console.log('Excel 数据:', jsonData);
    //     // 处理完操作后关闭对话框
    //     this.importDialogVisible = false;
    //     // 清空 workbook 对象
    //     this.workbook = null;
    //   } else {
    //     console.error('未找到 Excel 文件');
    //   }
    // },
//     submitImport() {
//   if (this.workbook) {
//     // 处理 Excel 数据
//     const firstSheetName = this.workbook.SheetNames[0];
//     const worksheet = this.workbook.Sheets[firstSheetName];
//     // 将 Excel 数据转换为 JSON 格式
//     const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
//     console.log('Excel 数据:', jsonData);
//     // 处理完操作后关闭对话框
//     this.importDialogVisible = false;
//     // 清空 workbook 对象
//     this.workbook = null;
//     this.previewImportData(jsonData);

//   } else {
//     console.error('未找到 Excel 文件');
//   }
// },

submitImport() {
  if (this.workbook) {
    // 处理 Excel 数据
    const firstSheetName = this.workbook.SheetNames[0];
    const worksheet = this.workbook.Sheets[firstSheetName];
    // 将 Excel 数据转换为 JSON 格式
    const jsonData = XLSX.utils.sheet_to_json(worksheet, {
      header: ["category", "title", "press", "publish_year", "author", "price", "stock"]
    });
    console.log('Excel 数据:', jsonData);

    // 显示预览对话框
    this.previewImportData(jsonData);
  } else {
    console.error('未找到 Excel 文件');
  }
},
// 预览导入数据
previewImportData(importData) {
  // 使用 Element Plus 对话框组件展示导入数据的预览
  this.$confirm({
    title: '预览导入数据',
    content: `
      <el-table :data="importData">
        <el-table-column prop="category" label="类别"></el-table-column>
        <el-table-column prop="title" label="书名"></el-table-column>
        <el-table-column prop="press" label="出版社"></el-table-column>
        <el-table-column prop="publish_year" label="年份"></el-table-column>
        <el-table-column prop="author" label="作者"></el-table-column>
        <el-table-column prop="price" label="价格"></el-table-column>
        <el-table-column prop="stock" label="初始库存"></el-table-column>
      </el-table>
    `,
    showCancelButton: true,
    showConfirmButton: true,
    cancelButtonText: '取消',
    confirmButtonText: '确认导入',
    dangerouslyUseHTMLString: true,
  }).then(() => {
    // 用户确认导入，执行真正的导入操作
    this.executeImport(importData);
  }).catch(() => {
    // 用户取消导入
    console.log('用户取消导入操作');
  });
},

    // 取消批量导入
    cancelImport() {
      // 关闭对话框并清空 workbook 对象
      this.importDialogVisible = false;
      this.workbook = null;
    },
  
    created() {
      this.fetchBooks()
    },
  computed: {
    isFormInvalid() {
      const { book_id, title, author, category, press, publish_year, price, stock } = this.newBookForm
      return !title || !author || !category || !press || !publish_year
    }
  }
}
</script>

<style scoped>

.cardBox {
  height: 300px;
  width: 200px;
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
  text-align: center;
  margin-top: 40px;
  margin-left: 27.5px;
  margin-right: 10px;
  padding: 7.5px;
  padding-right: 10px;
  padding-top: 15px;
  background-image: url('https://blog-pic-thorin.oss-cn-hangzhou.aliyuncs.com/163d9693d9b2c84d62458b2d142aa92.png');
  background-size: cover; /* 确保图片覆盖整个元素 */
  background-position: center; /* 将背景图片居中 */
}


.newCardBox {
  height: 300px;
  width: 200px;
  margin-top: 40px;
  margin-left: 27.5px;
  margin-right: 10px;
  padding: 7.5px;
  padding-right: 10px;
  padding-top: 15px;
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
  text-align: center;
  background-color: #e0f7fa; /* Light green color */
}


</style>


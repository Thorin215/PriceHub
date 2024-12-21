<template>
  <div class="login-container">
    <el-form ref="loginForm" class="login-form" auto-complete="on" label-position="left">
      <div class="example">
        <h1 class='title'>PriceHub</h1>
        <h2 class='title'>好价汇</h2>
        <h3 class="title">B/S程序设计大作业</h3>
      </div>

      <div class="form-area">
        <el-form :model="loginForm" class="login-form">
          <el-form-item label="用户角色">
            <el-select v-model="loginForm.id" placeholder="请选择用户角色" class="login-select" @change="selectGet"> 
              <el-option
                v-for="item in userList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              >
                <span style="float: left">{{ item.name }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">{{ item.id }}</span>
              </el-option>
            </el-select>
          </el-form-item>
  
          <el-form-item label="密码">
            <el-input type="password" v-model="loginForm.password" placeholder="请输入密码"></el-input>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 按钮容器 -->
      <div class="button-container">
        <el-button :loading="loading" type="primary" class="action-button" @click.native.prevent="handleLogin">登录</el-button>
        <el-button type="success" class="action-button" @click.native.prevent="showRegisterDialog">注册</el-button>
        <el-button type="warning" class="action-button" @click.native.prevent="showResetPasswordDialog">忘记密码</el-button>
      </div>

      <!-- 重置密码弹窗 -->
      <el-dialog title="重置密码" :visible.sync="resetPasswordDialogVisible" width="50%" center>
        <el-form ref="resetPasswordForm" :model="resetPasswordForm">
          <el-form-item label="邮箱">
            <el-input v-model="resetPasswordForm.email" placeholder="请输入注册时使用的邮箱"></el-input>
          </el-form-item>
          <el-form-item label="验证码">
            <el-input v-model="resetPasswordForm.verificationCode" placeholder="请输入收到的验证码"></el-input>
            <el-button type="primary" @click="sendVerificationCode">发送验证码</el-button>
          </el-form-item>
          <el-form-item label="新密码">
            <el-input type="password" v-model="resetPasswordForm.newPassword" placeholder="请输入新密码"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="resetPasswordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleResetPassword">确认重置</el-button>
        </div>
      </el-dialog>

      <!-- 注册弹窗 -->
      <el-dialog title="用户注册" :visible.sync="registerDialogVisible" width="50%" center>
        <el-form ref="registerForm" :model="registerForm">
          <el-form-item label="用户名">
            <el-input v-model="registerForm.name"></el-input>
          </el-form-item>
          <el-form-item label="用户ID">
            <el-input
              v-model="registerForm.id"
              placeholder="请输入用户ID（至少6个字符）"
              @input="checkUserIdLength"
            ></el-input>
            <span v-if="userIdError" class="error-message">用户ID长度至少为6个字符</span>
          </el-form-item>
          <el-form-item label="密码">
            <el-input
              type="password"
              v-model="registerForm.password"
              placeholder="请输入密码（至少6个字符）"
              @input="checkPasswordLength"
            ></el-input>
            <span v-if="passwordError" class="error-message">密码长度至少为6个字符</span>
          </el-form-item>
          <el-form-item label="邮箱">
            <div class="email-input-wrapper">
              <el-input
                v-model="registerForm.emailPrefix"
                placeholder="请输入邮箱前缀"
                suffix-icon="el-icon-tickets"
                style="width: 45%; margin-right: 10px;" 
              ></el-input>
              <el-select
                v-model="registerForm.emailSuffix"
                @change="updateEmail"
                placeholder="选择邮箱后缀"
                style="width: 35%;" 
              >
                <el-option
                  v-for="suffix in emailList"
                  :key="suffix"
                  :label="suffix"
                  :value="suffix"
                ></el-option>
              </el-select>
            </div>
          </el-form-item>
          <!-- 验证码输入框 -->
          <el-form-item label="验证码">
            <el-input v-model="registerForm.verificationCode" placeholder="请输入验证码"></el-input>
            <el-button type="primary" @click="sendVerificationCodeForRegister">发送验证码</el-button>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="registerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRegister">注册</el-button>
        </div>
      </el-dialog>
    </el-form>
  </div>
</template>

<script>
import { queryAllUsers, createUser, checkLogin, resetPassword, sendCode } from '@/api/user'

export default {
  name: 'Login',
  data() {
    return {
      loading: false,
      redirect: undefined,
      userList: [],
      loginForm: {
        id: '',
        password: ''
      },
      resetPasswordDialogVisible: false, // 控制重置密码弹窗显示
      resetPasswordForm: {
        email: '',
        verificationCode: '',
        newPassword: ''
      },
      registerDialogVisible: false, // 控制注册弹窗显示
      registerForm: {
        name: '',
        id: '',
        password: '',
        emailPrefix: '', // 邮箱前缀
        emailSuffix: '', // 邮箱后缀
        verificationCode: '' // 验证码
      },
      emailList: ['@gmail.com', '@yahoo.com', '@outlook.com', '@qq.com', '@163.com'],  // 常见邮箱后缀
      userIdError: false,
      passwordError: false
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    queryAllUsers().then(response => {
      if (response) {
        this.userList = response
        this.$message.success('数据加载成功')
      }
    })
  },

  methods: {
    handleLogin() {
      if (this.loginForm.id && this.loginForm.password) {
        this.loading = true
        checkLogin(this.loginForm).then(response => {
          if (response === '登录成功') {          
            this.$store.dispatch('user/login', this.loginForm.id).then(path => {
              this.$router.push({ path: path })
            })
            this.loading = false
          } else {
            this.$message.error('登录失败')
          }
        }).catch(() => {
          this.loading = false
        })
      } else {
        this.$message.error('请选择用户角色和输入密码')
      }
    },
    selectGet(userId) {
      this.loginForm.id = userId
    },
    showRegisterDialog() {
      this.registerDialogVisible = true
    },
    updateEmail() {
      this.registerForm.email = this.registerForm.emailPrefix + this.registerForm.emailSuffix; // 更新完整邮箱
    },
    validateEmailPrefix(prefix) {
      // 使用正则表达式验证邮箱前缀
      const regex = /^[a-zA-Z0-9][a-zA-Z0-9._]*[a-zA-Z0-9]$/;
      return regex.test(prefix);
    },
    checkUserIdLength() {
      this.userIdError = this.registerForm.id.length < 6;
    },
    checkPasswordLength() {
      this.passwordError = this.registerForm.password.length < 6;
    },
    sendVerificationCode() {
      sendCode(this.resetPasswordForm.email).then(response => {
        if (response === '验证码已发送') {          
          this.$message.success('验证码发送成功')
        } else {
          this.$message.error('验证码发送失败')
        }
      }).catch(() => {
        this.$message.error('验证码发送失败')
      })
    },
    sendVerificationCodeForRegister() {
      const email = this.registerForm.emailPrefix + this.registerForm.emailSuffix;
      sendCode(email).then(response => {
        if (response === '验证码已发送') {          
          this.$message.success('验证码发送成功')
        } else {
          this.$message.error('验证码发送失败')
        }
      }).catch(() => {
        this.$message.error('验证码发送失败')
      })
    },
    handleResetPassword() {
      resetPassword(this.resetPasswordForm).then(response => {
        if (response === '密码重置成功') {
          this.$message.success('密码重置成功')
          this.resetPasswordDialogVisible = false
        } else {
          this.$message.error('密码重置失败')
        }
      }).catch(() => {
        this.$message.error('密码重置失败')
      })
    },
    handleRegister() {
      if (this.registerForm.name && this.registerForm.id && this.registerForm.password) {
        // 验证用户ID和密码长度
        if (this.registerForm.id.length < 6) {
          this.$message.error('用户ID长度必须大于6个字符');
          return;
        }
        if (this.registerForm.password.length < 6) {
          this.$message.error('密码长度必须大于6个字符');
          return;
        }

        // 验证邮箱前缀
        if (!this.validateEmailPrefix(this.registerForm.emailPrefix)) {
          this.$message.error('邮箱前缀无效，请只使用字母、数字、下划线和点号，且必须以字母或数字开头和结尾');
          return;
        }

        this.registerForm.email = this.registerForm.emailPrefix + this.registerForm.emailSuffix; // 拼接完整邮箱
        createUser(this.registerForm).then(() => {
          this.$message.success('注册成功');
          this.registerDialogVisible = false;
        }).catch(() => {
          this.$message.error('注册失败');
        });
      } else {
        this.$message.error('请填写所有字段');
      }
    },
    showResetPasswordDialog() {
      this.resetPasswordDialogVisible = true;
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100%;
  width: 100%;
  height: 100%;
  position: fixed;
  background-color: #f2f2f2; // 设置为淡灰色
  background-size: 100% 100%;
  overflow: hidden;
  display: flex;

  .login-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 0 35px;
    margin: 0 auto;
  }

  .button-container {
    display: flex;            // 使按钮横向排列
    justify-content: space-between;  // 两个按钮之间留空
    gap: 10px;                // 按钮之间的间距
    margin-bottom: 30px;
  }

  .action-button {
    flex: 1;                  // 使按钮均匀分布，占据相同宽度
  }

}
</style>
module.exports = {
  presets: [
    '@vue/app',
    '@babel/preset-env'
  ],
  plugins: [
    '@babel/plugin-proposal-class-properties' // 添加这个插件以支持类的静态属性
  ]
}

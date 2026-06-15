<template>
  <div class="dashboard">
    <div class="page-header">
      <h1>数据概览</h1>
      <span class="date">{{ today }}</span>
    </div>

    <div class="stat-cards">
      <div class="stat-card total">
        <div class="stat-icon">&#9632;</div>
        <div class="stat-body">
          <div class="stat-num">{{ stats.total }}</div>
          <div class="stat-label">学生总数</div>
        </div>
      </div>
      <div class="stat-card male">
        <div class="stat-icon">&#9632;</div>
        <div class="stat-body">
          <div class="stat-num">{{ stats.male }}</div>
          <div class="stat-label">男生人数</div>
        </div>
      </div>
      <div class="stat-card female">
        <div class="stat-icon">&#9632;</div>
        <div class="stat-body">
          <div class="stat-num">{{ stats.female }}</div>
          <div class="stat-label">女生人数</div>
        </div>
      </div>
    </div>

    <div class="chart-section">
      <h3>性别比例</h3>
      <div class="bar-chart">
        <div class="bar-row">
          <span class="bar-label">男生</span>
          <div class="bar-track">
            <div class="bar-fill male-bar" :style="{ width: malePercent + '%' }">{{ stats.male }}</div>
          </div>
        </div>
        <div class="bar-row">
          <span class="bar-label">女生</span>
          <div class="bar-track">
            <div class="bar-fill female-bar" :style="{ width: femalePercent + '%' }">{{ stats.female }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getStats } from '../api/student'

export default {
  data() {
    return { stats: { total: 0, male: 0, female: 0 } }
  },
  computed: {
    today() {
      return new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
    },
    malePercent() {
      return this.stats.total > 0 ? Math.round(this.stats.male / this.stats.total * 100) : 0
    },
    femalePercent() {
      return this.stats.total > 0 ? Math.round(this.stats.female / this.stats.total * 100) : 0
    }
  },
  async mounted() {
    try {
      const { data } = await getStats()
      if (data.code === 200) {
        this.stats = data.data
      }
    } catch (e) { console.error(e) }
  }
}
</script>

<style scoped>
.dashboard { max-width: 900px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 28px; }
.page-header h1 { font-size: 24px; font-weight: 700; color: #1a1a2e; }
.date { font-size: 14px; color: #999; }
.stat-cards { display: flex; gap: 20px; margin-bottom: 32px; }
.stat-card {
  flex: 1; background: #fff; border-radius: 16px; padding: 24px;
  display: flex; align-items: center; gap: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}
.stat-card:hover { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(0,0,0,0.1); }
.stat-icon {
  width: 56px; height: 56px; border-radius: 14px;
  display: flex; align-items: center; justify-content: center;
  font-size: 20px; color: #fff;
}
.stat-card.total .stat-icon { background: linear-gradient(135deg, #667eea, #764ba2); }
.stat-card.male .stat-icon { background: linear-gradient(135deg, #43e97b, #38f9d7); }
.stat-card.female .stat-icon { background: linear-gradient(135deg, #f093fb, #f5576c); }
.stat-num { font-size: 36px; font-weight: 700; color: #1a1a2e; line-height: 1; }
.stat-label { font-size: 13px; color: #999; margin-top: 6px; }
.chart-section {
  background: #fff; border-radius: 16px; padding: 28px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}
.chart-section h3 { font-size: 16px; font-weight: 600; color: #1a1a2e; margin-bottom: 24px; }
.bar-row { display: flex; align-items: center; gap: 16px; margin-bottom: 16px; }
.bar-label { width: 36px; font-size: 14px; color: #666; font-weight: 500; }
.bar-track {
  flex: 1; height: 32px; background: #f5f5f5; border-radius: 8px; overflow: hidden;
}
.bar-fill {
  height: 100%; border-radius: 8px; display: flex; align-items: center; justify-content: flex-end;
  padding-right: 12px; font-size: 13px; font-weight: 600; color: #fff;
  min-width: 40px; transition: width 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}
.male-bar { background: linear-gradient(90deg, #43e97b, #38f9d7); }
.female-bar { background: linear-gradient(90deg, #f093fb, #f5576c); }
</style>

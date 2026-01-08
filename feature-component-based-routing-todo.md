# MGMC 重构计划：基于组件的精准路由系统 (Unity/UE 理念)

本项目旨在将 MGMC 从现有的“全量广播”模式重构为“基于 ID 映射的精准分发”模式，大幅提升性能并优化开发体验。

## 核心理念
- **组件化 (Component-based)**：蓝图不再是全局运行的脚本，而是挂载在特定 MC ID 上的逻辑组件。
- **路由分发 (Routing)**：事件触发时，仅查询并运行与该 ID 关联的蓝图，实现 O(1) 或 O(logN) 的调度开销。
- **层级管理 (Hierarchy)**：支持 `Global -> Tag -> ID` 三层过滤机制。

## 任务清单

### 第一阶段：基础设施建设 (Infrastructure)
- [ ] 设计并实现 `ltd.opens.mg.mc.core.blueprint.routing.BlueprintRouter` 中央调度类。
- [ ] 实现 `mappings.json` 的持久化存储逻辑（存放在 `mgmc_blueprints/` 目录下）。
- [ ] 定义虚拟 ID 规范（例如 `mgmc:global`, `mgmc:players`）。
- [ ] 编写基础的映射管理 API（添加/删除映射、获取指定 ID 的蓝图列表）。

### 第二阶段：执行引擎重构 (Dispatching Refactor)
- [ ] 重构 `MaingraphforMC.java` 中的事件监听器（BlockBreak, PlayerMove 等）。
- [ ] 将监听器中的 `getAllBlueprints()` 遍历逻辑替换为 `BlueprintRouter.getMappedBlueprints(id)`。
- [ ] 实现事件位图过滤：如果映射的蓝图中不包含当前触发的事件节点，则不启动执行引擎。
- [ ] 优化 `on_player_move` 的源头调度（仅在有蓝图订阅移动事件时才启用监听）。

### 第三阶段：管理界面实现 (GUI Development)
- [ ] 参考 `ui_mockup.html` 实现 `BlueprintMappingScreen`。
- [ ] **左侧面板**：实现可滚动的 ID 订阅列表，支持搜索和分类（系统、方块、实体）。
- [ ] **右侧面板**：实现蓝图绑定列表，支持从现有蓝图库中选择并关联。
- [ ] **交互逻辑**：实现“新建定义”弹窗及 ESC 退出时的保存询问提醒。
- [ ] 注册管理界面的打开快捷键（建议 `Ctrl + Alt + M`）或指令。

### 第四阶段：高级特性与优化 (Advanced Features)
- [ ] 支持 Minecraft Tags (#) 映射逻辑。
- [ ] 实现蓝图实例的“局部变量”持久化（初步构思：利用 Data Components）。
- [ ] 性能压测：对比重构前后在 1000 个蓝图加载下的服务器 TPS 表现。

---
*注：本计划在 `feature/component-based-routing` 分支下执行。*

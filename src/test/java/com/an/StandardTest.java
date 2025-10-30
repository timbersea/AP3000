package com.an;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring Boot 标准单元测试模板
 * 用途：验证测试环境是否正常（执行 `mvn test` 时应显示 Tests run: 1）
 */
@SpringBootTest // 加载 Spring Boot 上下文（简化版，不启动服务器）
public class StandardTest { // 类名以 Test 结尾，符合 Surefire 识别规则

    // 测试方法：public void + 无参数 + @Test 注解（JUnit 5）
    @Test
    public void testBasicFunction() {
        // 简单断言：验证测试框架是否正常工作
        String message = "测试执行成功";
        assertNotNull(message, "消息不应为 null");
        assertEquals(5, 2 + 3, "基础计算断言失败");
        
        // 控制台输出，便于在日志中确认测试执行
        System.out.println("✅ 标准测试模板执行成功！");
    }
}
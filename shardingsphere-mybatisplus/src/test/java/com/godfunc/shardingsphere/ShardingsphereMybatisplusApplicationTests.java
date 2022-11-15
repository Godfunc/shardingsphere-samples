package com.godfunc.shardingsphere;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.godfunc.shardingsphere.dto.OrderInfoDTO;
import com.godfunc.shardingsphere.dto.UserOrderDTO;
import com.godfunc.shardingsphere.entity.ConfigEntity;
import com.godfunc.shardingsphere.entity.OrderDetailEntity;
import com.godfunc.shardingsphere.entity.OrderEntity;
import com.godfunc.shardingsphere.entity.UserEntity;
import com.godfunc.shardingsphere.service.ConfigService;
import com.godfunc.shardingsphere.service.OrderDetailService;
import com.godfunc.shardingsphere.service.OrderService;
import com.godfunc.shardingsphere.service.UserService;
import com.godfunc.shardingsphere.util.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class ShardingsphereMybatisplusApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ConfigService configService;

    @Test
    public void createUser() {
        UserEntity user = new UserEntity("godfunc12", new Date());
        user.setUserId(IdUtils.getId(1L, 3L));
        userService.save(user);
        log.info("save User Id = {}", user.getUserId());
    }

    @Test
    public void selectUser() {
        List<UserEntity> list = userService.list();
        log.info("list = {}", list);
    }

    /**
     * 添加
     */
    @Test
    void contextLoads() {
        for (int i = 0; i < 10; i++) {
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setAmount(2120L);
            orderEntity.setCreateTime(new Date());
            orderEntity.setUserId(1555553089619308545L);
            orderService.save(orderEntity);
            OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
            orderDetailEntity.setCreateTime(new Date());
            orderDetailEntity.setNum(2);
            orderDetailEntity.setUserId(orderEntity.getUserId());
            orderDetailEntity.setOrderId(orderEntity.getId());
            orderDetailService.save(orderDetailEntity);

        }
    }

    /**
     * 事务，插入某一条数据的时候抛出异常
     */
    @Test
    public void createOrder() {
        orderService.createOrder();
    }

    /**
     * 查询
     */
    @Test
    public void page() {
        IPage page = orderService.page1(1, 10, DateTime.now().plusDays(-1).toDate());
        log.info("total={}", page.getTotal());
        log.info("pages={}", page.getPages());
        log.info("data={}", page.getRecords());

    }

    /**
     * 查询 in
     */
    @Test
    public void orderList() {
        List<OrderInfoDTO> list = orderService.getListByIds(Arrays.asList(1518159472126681091L, 1518159472059572226L, 1518159385992376322L, 1518159386126594051L));
        log.info("get order list by ids = {}", list);
    }

    /**
     * 统计
     */
    @Test
    public void orderSum() {
        // 由于t_user 只存在于 m0，不会进行跨库查询，所以执行两句sql，也就是只会在m0中查询
        List<UserOrderDTO> list = orderService.getUserOrder();
        log.info("user order count = {}", list);
    }

    /**
     * like
     */
    @Test
    public void userList() {
        List<UserEntity> list = userService.list("godfunc");
        log.info("select user by name result = {}", list);
    }

    /**
     * 广播表
     */
    @Test
    public void config() {
        for (int i = 0; i < 5; i++) {
            ConfigEntity configEntity = new ConfigEntity();
            configEntity.setName("key" + i);
            configEntity.setValue("key" + i);
            configService.save(configEntity);
        }

    }

    /**
     * 查询广播表
     */
    @Test
    public void configList() {
        List<ConfigEntity> list = configService.list();
        log.info("config list = {}", list);
    }
}

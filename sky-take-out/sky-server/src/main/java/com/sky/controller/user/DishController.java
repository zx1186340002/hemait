package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate<String,Object> redistTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("菜单id{}",categoryId);
        //构造redis的key
        String key = "dish_"+categoryId;
        //查询redis是否存在如果存在直接返回，不存在再查询数据库，查询数据库直接返回的是里面所有的string类型。
        List<DishVO> list  = (List<DishVO>) redistTemplate.opsForValue().get(key);
        if (list !=null && list.size()>0) {
            return Result.success(list);
        }
        //通过数据库查询
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品
        log.info("<UNK>id{}",categoryId);
         list = dishService.listWithFlavor(dish);
         log.info("执行到这一步了：{}",list);
         redistTemplate.opsForValue().set(key,list);
         //将所有string组合在一起然后添加进去，需要注意的是list<DishVO>会自动转化。
        log.info("这里了{}",list);
        return Result.success(list);
    }

}

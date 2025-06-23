package com.sky.controller.admin;

import com.github.pagehelper.PageInfo;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags =  "菜品相关内容")
public class DishController {
    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private DishService dishService;
    /**
     *新增菜品
     * @param dishDTO
     * @return
     */
    @ApiOperation("新增菜品")
    @PostMapping
    public Result save(DishDTO dishDTO) {
        String key = "dish"+dishDTO.getCategoryId();
       cleanCache(key);
        log.info("新增菜品{}",dishDTO);
       dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询{}",dishPageQueryDTO);
        PageResult  pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult) ;
    }

    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids){
    log.info("菜品批量删除{}",ids);
    //将所有的菜品缓存数据清理掉，所有dish_开头的key
         cleanCache("dish_*");
        //因为支持穿入一个集合类型的数据所以可以实现一次新全部删除以dish开头的数据。
         dishService.deleteBatch(ids);
    return Result.success();
    }

    /**
     * 根据菜品来查询口味数据。
     * @param id
     * @return
     */

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById( @PathVariable  Long id){
        log.info("查询菜品{}",id);
        DishVO dishVo= dishService.getByIdWithFlavor(id);
        return Result.success(dishVo) ;
    }

    /**
     * 修改菜品。
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        //因为修改可能会造成一个或者多个受到影响，所以直接全部删除。
        cleanCache("dish_*");

        log.info("{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id){
        cleanCache("dish_*");
        dishService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 清理缓存的方法。
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}

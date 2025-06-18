package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SteamlDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.service.SetmealDishMapper;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;

@Service
@Slf4j
public class DishServiceimpl implements DishService {

    @Autowired
    private DishMapper  dishMapper;

    @Autowired
   private SteamlDishMapper  steamlDishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品和口味
     * @param dishDTO
     */
    @Transactional       //添加事务注解因为涉及了两个表格。
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //向菜品表插入1条数据。
        dishMapper.insert(dish);
        //向口味插入n条数据。
        Long id = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors!=null&&flavors.size()>0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page =   dishMapper.pageQuery(dishPageQueryDTO);
        PageResult pageResult = new PageResult(page.getTotal(),page.getResult());
        return pageResult;
    }

    /**
     * 实现菜品批量删除
     * @param ids
     */
    @Override
    @Transactional  //添加事物注解防止删除失败
    public void deleteBatch(List<Long> ids) {
        //先判断是否能够删除。
        for(Long id:ids){
           Dish dish = dishMapper.getById(id);
           if(dish.getStatus()== StatusConstant.ENABLE){
               throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
           }

        }
        //菜品是否被关联。
        List<Long>  setmealIds = steamlDishMapper.getSetmealIdByDishIDs(ids);
        if(setmealIds!=null&&setmealIds.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除关联的口味数据。
//        ids.forEach(dishId -> {
//            dishMapper.deletById(dishId);
//            dishFlavorMapper.deleteByDishId(dishId);
//            使用批量删除，大大优化发给数据库的SQL数量。
//
//        });

        dishMapper.deletByIds(ids);
        dishFlavorMapper.deleteByDishIdd(ids);
        //优化后之会发送两条SQL。
    }

    /**
     * 根据id查询菜品和口味
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
       Dish dish = dishMapper.getById(id);
       List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
       DishVO dishVO = new DishVO();
       BeanUtils.copyProperties(dish,dishVO);
       dishVO.setFlavors(flavors);
       return dishVO;
    }

    /**
     * 修改口味进本信息和实现。
     * @param dishDTO
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //修改基本信息。
        dishMapper.update(dish);
        //将原先口味全部删掉后再重新插入。
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors!=null&&flavors.size()>0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }


}

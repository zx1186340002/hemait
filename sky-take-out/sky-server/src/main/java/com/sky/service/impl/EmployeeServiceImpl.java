package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 明文密码进行加密处理。
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }


    /**
     * 新增员工
     * @param employeeDTO
     */
    public void save(EmployeeDTO employeeDTO) {
        System.out.println("Service的线程id"+Thread.currentThread().getId());
        Employee employee = new Employee();
        //对象的属性拷贝，从前面拷贝到后面，前提属性名称一致性。
        BeanUtils.copyProperties(employeeDTO, employee);
        //设置剩余的属性
        employee.setStatus(StatusConstant.ENABLE);

        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        employeeMapper.insert(employee);
    }
    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    public PageResult pageQuery (EmployeePageQueryDTO employeePageQueryDTO) {
        //会拦截你接下来第一个查询语句，在执行之前自动拼接分页 SQL（即自动加 LIMIT 和 OFFSET），并包装返回结果为 Page<T>。
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        //Page表示的是一个ArryList的集合。,底层的实现依旧是threadLocal。
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        long total = page.getTotal();
        List<Employee> record = page.getResult();
        return new PageResult(total,record);
    }

    /**
     * 启用禁止员工账号
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id){
        Employee employee = Employee.builder()
                            .status(status)
                            .id(id)
                            .build();

        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
   public Employee getById(Long id){
      Employee employee = employeeMapper.getById(id);
      employee.setPassword("****");
       return employee;
    }

    /**
     * 编辑员工信息。
     * @Param employeeDTO
     * @return
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //对象的属性拷贝。
        BeanUtils.copyProperties(employeeDTO,employee);
        employeeMapper.update(employee);
    }
}

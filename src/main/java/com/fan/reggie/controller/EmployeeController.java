package com.fan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fan.reggie.common.R;
import com.fan.reggie.entity.Employee;
import com.fan.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequestMapping("/employee")
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @RequestMapping("/getAll")
    public List<Employee> getEmployee(){
        List<Employee> list = employeeService.list();
        return list;
    }

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        Employee e = employeeService.getOne(new LambdaQueryWrapper<Employee>().eq(Employee::getUsername, employee.getUsername()));
        if (e == null){
            return R.error("用户不存在");
        }

        if (!e.getPassword().equals(password)){
            return R.error("密码错误");
        }

        if (e.getStatus() == 0){
            return R.error("账号已禁用");
        }

        request.getSession().setAttribute("employee",e.getId());
        request.getSession().setAttribute("sessionTime",LocalDateTime.now());
        return R.success(e);
    }


    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出登录");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("进入添加员工方法");
        log.info("当前线程id：{}",Thread.currentThread().getId());
        log.info("员工信息：{}",employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //从session里面get的数据默认是object类型，需要强转一下
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("员工添加成功");
    }

    /**
     * 前端传来分页数据和筛选参数
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("进入页面查询分页");
        log.info("当前线程id：{}",Thread.currentThread().getId());
        //使用mybatisplus自带的分页插件，使用前需要先定义一个分页配置类放到config类里面
        //定义一个page类，里面封装了分页的全部信息，包括页码，和records数据，因为执行IService里面的page接口的page方法，会自动把数据放到page里面的records里
        Page pageInfo = new Page(page, pageSize);

        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

        employeeService.page(pageInfo, wrapper);

        return R.success(pageInfo);
    }

    /**
     * 功能：修改员工信息
     * 前端传来员工信息
     * 获取当前修改者的session，传入修改者id
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> put(HttpServletRequest request,@RequestBody Employee employee){
        log.info("修改员工信息中");
        log.info("当前线程id：{}",Thread.currentThread().getId());
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);
        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success("修改成功");
    }


    @GetMapping("/{id}")
    public R<Employee> get(@PathVariable Long id){
        log.info("根据id查询员工信息");
        log.info("当前线程id：{}",Thread.currentThread().getId());
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("未查询到该员工");
    }
}

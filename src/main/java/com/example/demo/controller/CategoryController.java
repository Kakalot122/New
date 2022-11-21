package com.example.demo.controller;

import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("categories")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping()
    public String list(ModelMap model,@RequestParam Optional<String> message){
        Iterable<Category> list = categoryRepository.findAll();


        if (message.isPresent()){
            model.addAttribute("message",message.get());
        }
        model.addAttribute("categories",list);
        return "category/list";
    }

    @GetMapping("sort")
    public String sort(ModelMap model, @RequestParam Optional<String> message,
                       @SortDefault(sort = "name",direction = Sort.Direction.ASC) Sort sort){
        Iterable<Category> list = categoryRepository.findAll(sort);


        if (message.isPresent()){
            model.addAttribute("message",message.get());
        }
        model.addAttribute("categories",list);
        return "category/sort";
    }


    @GetMapping("paginate")
    public String paginate(ModelMap model, @RequestParam Optional<String> message,
                          @PageableDefault(size = 5,sort = "name",direction = Sort.Direction.ASC) Pageable pageable){
        Page<Category> pages = categoryRepository.findAll(pageable);


        if (message.isPresent()){
            model.addAttribute("message",message.get());
        }

        List<Sort.Order> sortOrders = pages.getSort().stream().collect(Collectors.toList());


        if (sortOrders.size() > 0){
            Sort.Order order = sortOrders.get(0);
            model.addAttribute("sort",order.getProperty() + ","
            + (order.getDirection() == Sort.Direction.ASC?"asc" : "desc"));
        }else {

            model.addAttribute("sort","name");
        }
        model.addAttribute("pages",pages);
        return "category/paginate";
    }

    @GetMapping(value = {"newOrEdit","newOrEdit/{id}"})
    public String newOrEdit(ModelMap model,
                            @PathVariable(name = "id", required = false) Optional<Long> id){
        Category category;
        if(id.isPresent()){
            Optional<Category> existedCate = categoryRepository.findById(id.get());

            category = existedCate.isPresent()?existedCate.get(): new Category();

        }else {

            category = new Category();
        }
        model.addAttribute("category" , category);
        return "category/newOrEdit";
    }

    @PostMapping("saveOrUpdate")
    public String saveOrUpdate(ModelMap model, Category item){
        categoryRepository.save(item);
        model.addAttribute("message","New Category is saved");

        return "category/newOrEdit";
    }





}

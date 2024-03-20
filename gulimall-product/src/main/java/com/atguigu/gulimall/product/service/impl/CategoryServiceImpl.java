package com.atguigu.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //先拿到所有的分类数据
        List<CategoryEntity> all = list();
        //组装成父子的树形结构
        List<CategoryEntity> collect = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == 0;
        }).map(categoryEntity -> {
            categoryEntity.setChildren(selectChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((cate1, cate2) -> {
            return (cate1.getSort() == null ? 0 : cate1.getSort()) - (cate2.getSort() == null ? 0 : cate2.getSort());
        }).collect(Collectors.toList());
        return collect;
    }

    private List<CategoryEntity> selectChildren(CategoryEntity category, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == category.getCatId();
        }).map(categoryEntity -> {
            categoryEntity.setChildren(selectChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((cate1, cate2) -> {
            return (cate1.getSort() == null ? 0 : cate1.getSort()) - (cate2.getSort() == null ? 0 : cate2.getSort());
        }).collect(Collectors.toList());
        return children;
    }

}
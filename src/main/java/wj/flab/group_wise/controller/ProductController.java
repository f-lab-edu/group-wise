package wj.flab.group_wise.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import wj.flab.group_wise.service.ProductService;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


}

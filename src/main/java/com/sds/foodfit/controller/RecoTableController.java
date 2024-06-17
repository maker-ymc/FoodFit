package com.sds.foodfit.controller;

import com.sds.foodfit.domain.FoodDB;
import com.sds.foodfit.model.food.FoodDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RecoTableController {

    private final FoodDBService foodDBService;

    @Autowired
    public RecoTableController(@Qualifier("tableServiceImpl") FoodDBService foodDBService) {
        this.foodDBService = foodDBService;
    }

    @GetMapping("/insert")
    public String showInsertPage(Model model) {
        return "recotable/insert";
    }

    @GetMapping("/foodpopup")
    public String showFoodPopup() {
        return "recotable/foodpopup";
    }

    @GetMapping("/api/foods")
    @ResponseBody
    public List<FoodDB> getAllFoods(@RequestParam(value = "search", required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return foodDBService.searchFoodsByName(search);
        } else {
            return foodDBService.getAllFoods();
        }
    }
    @PostMapping("/calculate")
    public String calculate(@RequestParam("selectedFoods") String selectedFoods,
                            @RequestParam("protein") float protein,
                            @RequestParam("fat") float fat,
                            @RequestParam("carbohydrate") float carbohydrate,
                            @RequestParam("height") int height,
                            @RequestParam("weight") int weight,
                            @RequestParam("gender") String gender,
                            @RequestParam("age") int age,
                            Model model) {

        System.out.println("selectedFoods: " + selectedFoods);

        // 음식 이름에서 불필요한 공백 및 '삭제' 제거
        List<String> foodNames = List.of(selectedFoods.split(","))
                                      .stream()
                                      .map(String::trim)
                                      .map(name -> name.replace(" 삭제", "")) // 배열에서 제거하기 위해서 '삭제' 버튼의 삭제를 문자열로 인식해서 제거 
                                      .collect(Collectors.toList());

        System.out.println("foodNames: " + foodNames);

        List<FoodDB> foods = foodNames.stream()
                .flatMap(name -> {
                    List<FoodDB> foundFoods = foodDBService.findByFoodName(name);
                    System.out.println("Found foods for " + name + ": " + foundFoods);
                    return foundFoods.stream();
                })
                .collect(Collectors.toList());

        System.out.println("foods: " + foods);

        // mapToDouble을 사용하여 float 값을 합산
        float totalProtein = (float) foods.stream().mapToDouble(FoodDB::getProtein).sum();
        float totalFat = (float) foods.stream().mapToDouble(FoodDB::getFat).sum();
        float totalCarbohydrate = (float) foods.stream().mapToDouble(FoodDB::getCarbohydrate).sum();
        float totalKcal = (float) foods.stream().mapToDouble(FoodDB::getKcal).sum();

        System.out.println("totalProtein: " + totalProtein);
        System.out.println("totalFat: " + totalFat);
        System.out.println("totalCarbohydrate: " + totalCarbohydrate);
        System.out.println("totalKcal: " + totalKcal);

        double bmr;
        if (gender.equals("male")) {
            bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        } else {
            bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }

        int dailyCalories = (int) Math.round(bmr * 1.55);

        model.addAttribute("totalKcal", totalKcal);
        model.addAttribute("dailyCalories", dailyCalories);
        model.addAttribute("totalProtein", totalProtein);
        model.addAttribute("totalFat", totalFat);
        model.addAttribute("totalCarbohydrate", totalCarbohydrate);

        model.addAttribute("inputCarbohydrate", carbohydrate);
        model.addAttribute("inputProtein", protein);
        model.addAttribute("inputFat", fat);

        return "recotable/result";
    }
}

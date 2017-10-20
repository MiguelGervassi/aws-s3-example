package aws.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping("/")
    String index(Model model) {
        try {
            model.addAttribute("buckets", S3Controller.listBuckets());
            model.addAttribute("keys", KMSController.listKeys());
            model.addAttribute("aliases", KMSController.listAliases());
        } catch (Exception e) {
            System.out.println("Error while loading list of buckets: " + e);
        }
        return "index";
    }
}

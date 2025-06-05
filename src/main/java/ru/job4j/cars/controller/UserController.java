package ru.job4j.cars.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cars.GlobalExceptionMessage;
import ru.job4j.cars.exception.NotFoundException;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.UserService;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final String EXCEPTION = GlobalExceptionMessage.GLOBAL_EXCEPTION_MESSAGE.toString();

    @GetMapping("/login")
    public String getLoginPage() {
        return "user/login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute User user, Model model, HttpServletRequest request) {
        try {
            User userToLogin = userService.findByLoginAndPassword(user.getLogin(), user.getPassword());
            request.getSession().setAttribute("user", userToLogin);
            return "redirect:/";
        } catch (IllegalArgumentException | NotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "user/login";
        } catch (Exception e) {
            model.addAttribute("error", EXCEPTION);
            return "errors/404";
        }
    }

    @GetMapping("/register")
    public String register() {
        return "user/register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute User user, Model model) {
        try {
            userService.save(user);
            return "redirect:/user/login";
        } catch (NotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "user/register";
        } catch (Exception e) {
            model.addAttribute("error", EXCEPTION);
            return "errors/404";
        }
    }

    @GetMapping("/logout")
    public String makeLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/login";
    }
}

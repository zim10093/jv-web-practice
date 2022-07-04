package mate.controller.car;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mate.lib.Injector;
import mate.model.Car;
import mate.model.Driver;
import mate.service.CarService;
import mate.service.DriverService;

@WebServlet(urlPatterns = "/cars/drivers")
public class AllDriverFromCarController extends HttpServlet {
    private static final int INDEX_OF_FIRST_PARAM = 0;
    private static final Injector injector = Injector.getInstance("mate");
    private final DriverService driverService = (DriverService)
            injector.getInstance(DriverService.class);
    private final CarService carService = (CarService)
            injector.getInstance(CarService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Car car = carService.get(Long.parseLong(req.getParameter("id")));
        req.setAttribute("car", car);
        req.setAttribute("drivers", driverService.getAll());
        req.getRequestDispatcher("/WEB-INF/views/cars/drivers.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        Long carId = Long.parseLong(parameterMap.get("id")[INDEX_OF_FIRST_PARAM]);
        Car car = carService.get(carId);
        Map<Long, Driver> allDriversMap = driverService.getAll()
                .stream()
                .collect(Collectors.toMap(Driver::getId, Function.identity()));

        List<Driver> editedDrivers = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (entry.getKey().equals("id")) {
                continue;
            }
            Long driverId = Long.parseLong(entry.getKey());
            editedDrivers.add(allDriversMap.get(driverId));
        }

        car.setDrivers(editedDrivers);
        carService.update(car);
        resp.sendRedirect(req.getContextPath() + "/cars");
    }
}

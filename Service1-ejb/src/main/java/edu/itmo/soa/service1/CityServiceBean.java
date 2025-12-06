package edu.itmo.soa.service1;

import edu.itmo.soa.service1.dto.request.CityInput;
import edu.itmo.soa.service1.dto.request.CitySearchRequest;
import edu.itmo.soa.service1.dto.response.CitiesResponse;
import edu.itmo.soa.service1.dto.response.CityPageResponse;
import edu.itmo.soa.service1.entity.City;
import edu.itmo.soa.service1.entity.Coordinates;
import edu.itmo.soa.service1.entity.Human;
import edu.itmo.soa.service1.exception.CityAlreadyExistsException;
import edu.itmo.soa.service1.exception.CityNotFoundException;
import edu.itmo.soa.service1.exception.InvalidCityDataException;
import edu.itmo.soa.service1.util.CityMapper;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Stateless
public class CityServiceBean implements CityServiceRemote {
    @PersistenceContext(unitName = "CityPU")
    private EntityManager em;

    @Override
    public City findById(int id) {
        City c = em.find(City.class, id);
        if (c == null) throw new CityNotFoundException("ID", id);
        return c;
    }

    @Override
    public City createCity(CityInput input) {
        validateCityInput(input);

        Long count = em.createQuery("SELECT COUNT(c) FROM City c WHERE c.name = :name", Long.class)
                .setParameter("name", input.getName())
                .getSingleResult();

        if (count > 0) throw new CityAlreadyExistsException("Город с таким именем уже существует");

        City city = new City();
        city.setName(input.getName());
        city.setCoordinates(new Coordinates(
                input.getCoordinates().getX(),
                input.getCoordinates().getY()
        ));
        city.setArea(input.getArea());
        city.setPopulation(input.getPopulation());
        city.setMetersAboveSeaLevel(input.getMetersAboveSeaLevel());
        city.setEstablishmentDate(input.getEstablishmentDate());
        city.setPopulationDensity(input.getPopulationDensity());
        city.setGovernment(input.getGovernment());
        city.setGovernor(new Human(input.getGovernor().getAge()));

        em.persist(city);
        return city;
    }

    @Override
    public City updateCity(int id, CityInput input) {
        validateCityInput(input);

        City existing = findById(id);

        existing.setName(input.getName());
        existing.setCoordinates(new Coordinates(
                input.getCoordinates().getX(),
                input.getCoordinates().getY()
        ));
        existing.setArea(input.getArea());
        existing.setPopulation(input.getPopulation());
        existing.setMetersAboveSeaLevel(input.getMetersAboveSeaLevel());
        existing.setEstablishmentDate(input.getEstablishmentDate());
        existing.setPopulationDensity(input.getPopulationDensity());
        existing.setGovernment(input.getGovernment());
        existing.setGovernor(new Human(input.getGovernor().getAge()));

        return em.merge(existing);
    }

    @Override
    public void deleteById(int id) {
    }

    @Override
    public CityPageResponse searchCities(CitySearchRequest request) {

        List<City> allCities = em.createQuery("SELECT c FROM City c", City.class)
                .getResultList();

        List<City> filtered = new ArrayList<>();

        for (City city : allCities) {
            boolean matches = applyFilters(request, city);
            if (matches) filtered.add(city);
        }

        applySorting(request, filtered);

        int page = request.getPagination().getPage();
        int size = request.getPagination().getSize();

        int start = Math.min(page * size, filtered.size());
        int end = Math.min(start + size, filtered.size());
        List<City> pageContent = filtered.subList(start, end);

        CityPageResponse response = new CityPageResponse();
        CityPageResponse.Pagination p = new CityPageResponse.Pagination();

        p.setCurrentPage(page);
        p.setTotalPages((int) Math.ceil((double) filtered.size() / size));
        p.setPageSize(size);

        response.setPagination(p);
        response.setCities(CityMapper.tocityDtoList(pageContent));

        return response;
    }

    @Override
    public void deleteByMetersAboveSeaLevel(int meters) {
        List<City> res = em.createQuery(
                "SELECT c FROM City c WHERE c.metersAboveSeaLevel = :m",
                City.class
        ).setParameter("m", meters).getResultList();

        if (res.isEmpty())
            throw new CityNotFoundException("metersAboveSeaLevel", meters);

        em.remove(res.get(0));
    }

    @Override
    public CitiesResponse findByNamePrefix(String prefix) {
        if (prefix == null || prefix.isEmpty())
            throw new IllegalArgumentException("prefix не должен быть пустой");

        List<City> list = em.createQuery(
                        "SELECT c FROM City c WHERE c.name LIKE :p", City.class
                ).setParameter("p", prefix + "%")
                .getResultList();

        return new CitiesResponse(CityMapper.tocityDtoList(list));
    }

    @Override
    public CitiesResponse getCitiesByGovernorAge(int age) {
        if (age <= 0)
            throw new IllegalArgumentException("age должен быть > 0");

        List<City> list = em.createQuery(
                "SELECT c FROM City c WHERE c.governor.age > :age",
                City.class
        ).setParameter("age", age).getResultList();

        return new CitiesResponse(CityMapper.tocityDtoList(list));
    }

    private void validateCityInput(CityInput input) {
        if (input.getName() == null || input.getName().trim().isEmpty())
            throw new InvalidCityDataException("name пустой");

        if (input.getCoordinates() == null)
            throw new InvalidCityDataException("coordinates null");

        if (input.getCoordinates().getX() == null
                || input.getCoordinates().getX() > 220)
            throw new InvalidCityDataException("coordinates.x ошибка");

        if (input.getCoordinates().getY() == null)
            throw new InvalidCityDataException("coordinates.y null");

        if (input.getArea() == null || input.getArea() <= 0)
            throw new InvalidCityDataException("area ошибка");

        if (input.getPopulation() < 0)
            throw new InvalidCityDataException("population ошибка");

        if (input.getPopulationDensity() != null && input.getPopulationDensity() <= 0)
            throw new InvalidCityDataException("populationDensity ошибка");

        if (input.getGovernment() == null)
            throw new InvalidCityDataException("government null");

        if (input.getGovernor() == null || input.getGovernor().getAge() <= 0)
            throw new InvalidCityDataException("governor ошибка");
    }

    private boolean applyFilters(CitySearchRequest req, City c) {
        if (req == null || req.getFilter() == null) return true;

        CitySearchRequest.Filter f = req.getFilter();

        if (f.getName() != null) {
            if (c.getName() == null || !c.getName().contains(f.getName())) {
                return false;
            }
        }

        if (f.getCoordinates() != null) {
            CitySearchRequest.CoordinatesFilter cf = f.getCoordinates();

            if (cf.getX() != null) {
                if (!inRange(cf.getX(), c.getCoordinates().getX())) return false;
            }
            if (cf.getY() != null) {
                if (!inRange(cf.getY(), c.getCoordinates().getY())) return false;
            }
        }

        if (f.getCreationDate() != null) {
            ZonedDateTime creation = c.getCreationDate();
            if (!inDateRange(f.getCreationDate().getMin(), f.getCreationDate().getMax(), LocalDate.from(creation)))
                return false;
        }

        if (f.getArea() != null) {
            if (!inRange(f.getArea(), c.getArea())) return false;
        }

        if (f.getPopulation() != null) {
            if (!inRange(f.getPopulation(), c.getPopulation())) return false;
        }

        if (f.getMetersAboveSeaLevel() != null) {
            if (!inRange(f.getMetersAboveSeaLevel(), c.getMetersAboveSeaLevel())) return false;
        }

        if (f.getEstablishmentDate() != null) {
            LocalDateTime est = c.getEstablishmentDate();
            if (!inDateTimeRange(
                    f.getEstablishmentDate().getMin(),
                    f.getEstablishmentDate().getMax(),
                    est
            )) return false;
        }

        if (f.getPopulationDensity() != null) {
            if (!inRange(f.getPopulationDensity(), c.getPopulationDensity())) return false;
        }

        if (f.getGovernment() != null) {
            if (!c.getGovernment().name().equalsIgnoreCase(f.getGovernment()))
                return false;
        }

        if (f.getGovernor() != null && f.getGovernor().getAge() != null) {
            if (!inRange(f.getGovernor().getAge(), c.getGovernor().getAge()))
                return false;
        }

        return true;
    }

    private void applySorting(CitySearchRequest request, List<City> cities) {
        if (request.getSort() == null || request.getSort().getField() == null) {
            return;
        }

        String field = request.getSort().getField();
        boolean descending = request.getSort().getDirection() == CitySearchRequest.Sort.Direction.DESC;

        Comparator<City> comparator = Comparator.comparing(city -> {
            Object value;
            switch (field) {
                case "id": value = city.getId(); break;
                case "name": value = city.getName(); break;
                case "area": value = city.getArea(); break;
                case "population": value = city.getPopulation(); break;
                case "metersAboveSeaLevel": value = city.getMetersAboveSeaLevel(); break;
                case "populationDensity": value = city.getPopulationDensity(); break;
                case "government": value = city.getGovernment(); break;
                case "governor.age":
                    value = city.getGovernor() != null ? city.getGovernor().getAge() : null;
                    break;
                case "coordinates.x":
                    value = city.getCoordinates() != null ? city.getCoordinates().getX() : null;
                    break;
                case "coordinates.y":
                    value = city.getCoordinates() != null ? city.getCoordinates().getY() : null;
                    break;
                case "establishmentDate": value = city.getEstablishmentDate(); break;
                case "creationDate": value = city.getCreationDate(); break;
                default: value = null; break;
            }
            return (Comparable) value;
        }, Comparator.nullsLast(Comparator.naturalOrder()));

        if (descending) {
            comparator = comparator.reversed();
        }

        cities.sort(comparator);
    }



    private <T extends Comparable<T>> boolean inRange(CitySearchRequest.Range<T> range, T value) {
        if (value == null) return false;

        if (range.getMin() != null && value.compareTo(range.getMin()) < 0)
            return false;

        if (range.getMax() != null && value.compareTo(range.getMax()) > 0)
            return false;

        return true;
    }

    private boolean inDateRange(LocalDate min, LocalDate max, LocalDate value) {
        if (value == null) return false;

        if (min != null && value.isBefore(min)) return false;
        if (max != null && value.isAfter(max)) return false;

        return true;
    }

    private boolean inDateTimeRange(LocalDateTime min, LocalDateTime max, LocalDateTime value) {
        if (value == null) return false;

        if (min != null && value.isBefore(min)) return false;
        if (max != null && value.isAfter(max)) return false;

        return true;
    }
}

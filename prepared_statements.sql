
insert into address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) values (?, ?, ?, ?, ?, ?, ?, ?, ?);
update address set address=?, address2=?, cityId=?, postalCode=?, phone=?, createDate=?, createdBy=?, lastUpdate=?, lastUpdateBy=? where addressId=?;
delete from address where addressId=?;

insert into appointment(customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdateBy) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
update appointment set customerId=?, userId=?, title=?, description=?, location=?, contact=?, type=?, url=?, start=?, end=?, createDate=?, createdBy=?, lastUpdateBy=? where appointmentId=?;
delete from appointment where appointmentId=?;

insert into customer (customerName, addressId, active, createDate, createdBy, lastUpdateBy) values (?, ?, ?, ?, ?, ?);
update customer set customerName=?, addressId=?, active=?, lastUpdatedBy=? where customerId=?;
delete from customer where customerId=?;

insert into city (city, countryId, createDate, createdBy, lastUpdateBy) values (?,?,?,?,?);
update city set city=?, countryId=?, lastUpdatedBy=? where cityId=?;
delete from city where cityId=?;

insert into country (country, createDate, createdBy, lastUpdateBy) values (?, ?, ?, ?);
update country set country=?, lastUpdatedBy=? where countryId=?;
delete from country where countryId=?;

insert into user (userName, password, active, createDate, createdBy, lastUpdateBy) values (?,?,?,?,?,?);
update user set userName=?, password=?, active=?, lastUpdateBy=? where userId=?;
delete from user where userId=?;
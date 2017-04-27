======================
Git Setup
======================

create .gitignore file
	/target/*
	/.idea/*
	odata.iml

cd C:\heroku-workspace\odata
    git init
    git add .
    git commit -m "initial check in"
    git remote add origin https://github.com/spring-work/odata.git
    git push https://dfsco-cshah:Welcome1@github.com/DFSCO/odata.git

make some changes
    git checkout -b testbranch
    git add .
    git commit -m "branch check in"
    git push origin testbranch https://dfsco-cshah:Welcome1@github.com/DFSCO/odata.git
    git checkout master
    git pull origin master

git reset --hard origin/master *rescue*



======================
Heroku Setup
======================

cd C:\heroku-workspace\odata
    heroku login
    heroku create --app odata-cshah

goto app deploy -> connect to correct git repo

deploy directly or do new git push

goto app settings -> create config vars
or
    heroku config
    heroku config:set KEY=VALUE --app odata-cshah
    heroku config:unset KEY
    heroku config:get KEY

    heroku config:set HELLO_MESG=HMI2   --app odata-cshah

Add Addons for heroku postgres

cd C:\heroku-workspace\odata
heroku git:remote -app odata-cshah (this will map current repo to name)


database sql: use db visualsizer -> SQL commander or
heroku pg:psql


Run Below:
    create sequence odata_empl_seq START 101;
    create table employee (id integer default nextval('odata_empl_seq'), fname varchar(200), lname varchar(200), created timestamp default now() );
    insert into employee (fname, lname) values ('Chintan', 'Shah');
    insert into employee (fname, lname) values ('John', 'Doe');

    create sequence odata_addr_seq START 101;
    create table address (id integer default nextval('odata_addr_seq'), addrline varchar(200), zipcode varchar(200), country varchar(200), created timestamp default now() );
    insert into address (addrline, zipcode, country) values ('1 Main Street', '90004', 'USA');
    insert into address (addrline, zipcode, country) values ('2 Main Street', '90004', 'USA');


a=dlmread('../tmp.dat',',',1,0);

figure()
plot(a(:,2),a(:,4),a(:,2),a(:,5))

ds=[0;diff(a(:,3))];
dt=[1;diff(a(:,2))];
v=ds./dt;

figure()
plot(a(:,3),v)

a=dlmread('../tmpPos.dat',',',1,0);

figure()
plot(a(a(:,1)==1,2),a(a(:,1)==1,3))

ds=[0;diff(a(:,3))];
dt=[1;diff(a(:,2))];
v=ds./dt;

% figure()
% plot(a(:,3),v)

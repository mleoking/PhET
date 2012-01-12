## weak base/strong acid

function weakbase (Ca,Cb,Va,Vb,Kb)
Kw = 1e-14;

## solve cubic eqn for H: a*H^3 + b*H^2 + c*H + d = 0
a = -(Va + Vb);
b = Ca.*Va - Cb.*Vb - (Kw./Kb).*(Va + Vb);
c = Kw.*(Va + Vb) + (Ca.*Va.*Kw)./Kb;
d = ((Kw.^2)./Kb).*(Va + Vb);

#a = -Va - Vb;
#b = Ca.*Va - Cb.*Vb - Va.*Kw./Kb - Vb.*Kw./Kb;
#c = Va.*Kw + Vb.*Kw + Ca.*Va.*Kw./Kb;
#d = Va./Kb.*Kw.^2 + Vb./Kb.*Kw.^2;

for indx = 1:length(Va)
	x = roots([a(indx), b(indx), c(indx), d(indx)]);
	x = sort(x);
	H1(indx) = x(1);
	H2(indx) = x(2);
	H3(indx) = x(3);
end

## find pH at each Va value
#pH1 = -log10(H1);
#pH2 = -log10(H2);
pH3 = -log10(H3);

## plot pH versus Va
#plot(Va,pH1);
#plot(Va,pH2);
plot(Va,pH3); axis ([0,75,0,14]); xlabel('Va'); ylabel('pH');

endfunction
## strong base/strong acid

function strongbase (Ca,Cb,Va,Vb)
Kw = 1e-14;

## solve quadratic eqn for H: a*H^2 + b*H + c = 0;
a = -(Va + Vb);
b = Ca.*Va - Cb.*Vb;
c = Kw.*(Va + Vb);
#H1 = (-b + sqrt(b.^2 - 4.*a.*c))./(2.*a);
H2 = (-b - sqrt(b.^2 - 4.*a.*c))./(2.*a);

## find pH at each Va value
#pH1 = -log10(H1);
pH2 = -log10(H2);

## plot pH versus Va
#plot(Va,pH1);
plot(Va,pH2); axis ([0,75,0,14]); xlabel('Va'); ylabel('pH');

endfunction